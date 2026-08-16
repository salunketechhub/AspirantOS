import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { SyllabusService } from '../../services/syllabus.service';
import { ProgressService } from '../../services/progress.service';
import { AuthService } from '../../core/auth/auth.service';
import {
  ExamResponse,
  OptionalSubjectResponse,
  SubjectResponse,
  TopicResponse,
} from '../../models/syllabus.models';
import { ProgressStatus } from '../../models/progress.models';

@Component({
  selector: 'app-syllabus',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './syllabus.component.html',
  styleUrl: './syllabus.component.css',
})
export class SyllabusComponent implements OnInit {
  private readonly syllabusService = inject(SyllabusService);
  private readonly progressService = inject(ProgressService);
  readonly authService = inject(AuthService);

  // Core data signals
  readonly exams = signal<ExamResponse[]>([]);
  readonly selectedExam = signal<ExamResponse | null>(null);
  readonly subjects = signal<SubjectResponse[]>([]);
  readonly selectedSubject = signal<SubjectResponse | null>(null);
  readonly topics = signal<TopicResponse[]>([]);
  readonly optionals = signal<OptionalSubjectResponse[]>([]);

  // User progress mapping signal: topicId -> ProgressStatus
  readonly progressMap = signal<Record<string, ProgressStatus>>({});

  // User PYQ mapping signal: topicId -> Boolean
  readonly pyqMap = signal<Record<string, boolean>>({});

  // Navigation & interaction signals (3 main tabs: PRELIMS, MAINS, OPTIONALS)
  readonly activeTab = signal<'PRELIMS' | 'MAINS' | 'OPTIONALS'>('PRELIMS');
  readonly optionalViewMode = signal<'SOCIOLOGY_TRACKER' | 'CATALOGUE'>('SOCIOLOGY_TRACKER');
  readonly searchQuery = signal<string>('');

  // Status signals
  readonly isLoading = signal<boolean>(false);
  readonly isTopicsLoading = signal<boolean>(false);
  readonly errorMessage = signal<string | null>(null);
  readonly updatingTopicId = signal<string | null>(null);

  // Computed filtered topics based on search
  readonly filteredTopics = computed(() => {
    const query = this.searchQuery().trim().toLowerCase();
    const rawTopics = this.topics();

    if (!query) {
      return rawTopics;
    }

    return rawTopics.filter(
      (topic) =>
        topic.name.toLowerCase().includes(query) ||
        topic.code.toLowerCase().includes(query) ||
        (topic.description && topic.description.toLowerCase().includes(query))
    );
  });

  // Computed filtered optional subjects catalogue
  readonly filteredOptionals = computed(() => {
    const query = this.searchQuery().trim().toLowerCase();
    const list = this.optionals();

    if (!query) {
      return list;
    }

    return list.filter(
      (opt) =>
        opt.name.toLowerCase().includes(query) ||
        opt.code.toLowerCase().includes(query) ||
        (opt.description && opt.description.toLowerCase().includes(query))
    );
  });

  // Flat list of all topic IDs in current subject
  readonly allTopicIdsInSubject = computed(() => {
    return this.topics().map((t) => t.id);
  });

  // Total topics count in currently selected subject
  readonly subjectTotalTopics = computed(() => {
    return this.allTopicIdsInSubject().length;
  });

  // Completed topics count in currently selected subject
  readonly subjectCompletedTopics = computed(() => {
    const map = this.progressMap();
    return this.allTopicIdsInSubject().filter((id) => map[id] === 'COMPLETED').length;
  });

  // In-progress topics count in currently selected subject
  readonly subjectInProgressTopics = computed(() => {
    const map = this.progressMap();
    return this.allTopicIdsInSubject().filter((id) => map[id] === 'IN_PROGRESS').length;
  });

  // Not started topics count in currently selected subject
  readonly subjectNotStartedTopics = computed(() => {
    const total = this.subjectTotalTopics();
    const completed = this.subjectCompletedTopics();
    const inProgress = this.subjectInProgressTopics();
    return Math.max(0, total - completed - inProgress);
  });

  // Subject completion percentage
  readonly subjectCompletionPercentage = computed(() => {
    const total = this.subjectTotalTopics();
    const completed = this.subjectCompletedTopics();
    return total > 0 ? Math.round((completed / total) * 100) : 0;
  });

  // Subject PYQ solved count
  readonly subjectPyqSolvedCount = computed(() => {
    const map = this.pyqMap();
    return this.allTopicIdsInSubject().filter((id) => !!map[id]).length;
  });

  ngOnInit(): void {
    this.loadInitialData();
  }

  loadInitialData(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    // 1. Fetch user progress map & PYQ map in bulk
    this.progressService.getAllProgressMap().subscribe({
      next: (map) => this.progressMap.set(map || {}),
      error: () => {},
    });

    this.progressService.getAllPyqMap().subscribe({
      next: (map) => this.pyqMap.set(map || {}),
      error: () => {},
    });

    // 2. Fetch Exams
    this.syllabusService.getExams().subscribe({
      next: (examsList) => {
        this.exams.set(examsList);
        this.isLoading.set(false);

        // Default to Prelims
        const prelims = examsList.find((e) => e.code.toUpperCase() === 'PRELIMS') || examsList[0];
        if (prelims) {
          this.selectExam(prelims);
        }
      },
      error: (err) => {
        this.isLoading.set(false);
        this.errorMessage.set(
          err?.error?.message || err?.message || 'Failed to load syllabus. Please check backend connection.'
        );
      },
    });

    // 3. Preload Optionals Catalogue
    this.syllabusService.getOptionals().subscribe({
      next: (opts) => this.optionals.set(opts),
      error: () => {},
    });
  }

  selectTab(tab: 'PRELIMS' | 'MAINS' | 'OPTIONALS'): void {
    this.activeTab.set(tab);
    this.searchQuery.set('');

    const matchedCode = tab === 'OPTIONALS' ? 'OPTIONAL' : tab;
    const matchedExam = this.exams().find((e) => e.code.toUpperCase() === matchedCode);

    if (matchedExam) {
      this.selectExam(matchedExam);
    }
  }

  setOptionalView(mode: 'SOCIOLOGY_TRACKER' | 'CATALOGUE'): void {
    this.optionalViewMode.set(mode);
    this.searchQuery.set('');
  }

  selectExam(exam: ExamResponse): void {
    this.selectedExam.set(exam);
    if (exam.code.toUpperCase() === 'PRELIMS') {
      this.activeTab.set('PRELIMS');
    } else if (exam.code.toUpperCase() === 'MAINS') {
      this.activeTab.set('MAINS');
    } else if (exam.code.toUpperCase() === 'OPTIONAL') {
      this.activeTab.set('OPTIONALS');
    }

    this.isTopicsLoading.set(true);
    this.errorMessage.set(null);

    this.syllabusService.getSubjectsByExam(exam.id).subscribe({
      next: (subjectsList) => {
        this.subjects.set(subjectsList);

        if (subjectsList.length > 0) {
          this.selectSubject(subjectsList[0]);
        } else {
          this.selectedSubject.set(null);
          this.topics.set([]);
          this.isTopicsLoading.set(false);
        }
      },
      error: (err) => {
        this.isTopicsLoading.set(false);
        this.errorMessage.set(err?.error?.message || 'Failed to load subjects for this exam.');
      },
    });
  }

  selectSubject(subject: SubjectResponse): void {
    this.selectedSubject.set(subject);
    this.isTopicsLoading.set(true);
    this.errorMessage.set(null);

    this.syllabusService.getTopicsBySubject(subject.id).subscribe({
      next: (topicList) => {
        this.topics.set(topicList);
        this.isTopicsLoading.set(false);
      },
      error: (err) => {
        this.isTopicsLoading.set(false);
        this.errorMessage.set(err?.error?.message || 'Failed to load topics.');
      },
    });
  }

  getTopicStatus(topicId: string): ProgressStatus {
    return this.progressMap()[topicId] || 'NOT_STARTED';
  }

  isTopicPyqDone(topicId: string): boolean {
    return !!this.pyqMap()[topicId];
  }

  onStatusChange(topicId: string, newStatus: ProgressStatus): void {
    const previousStatus = this.getTopicStatus(topicId);
    if (previousStatus === newStatus) return;

    // Optimistic UI mutation
    this.progressMap.update((map) => ({ ...map, [topicId]: newStatus }));
    this.updatingTopicId.set(topicId);

    this.progressService.updateTopicProgress(topicId, newStatus).subscribe({
      next: (res) => {
        this.progressMap.update((map) => ({ ...map, [topicId]: res.status }));
        if (res.pyqDone !== undefined) {
          this.pyqMap.update((map) => ({ ...map, [topicId]: res.pyqDone }));
        }
        this.updatingTopicId.set(null);
      },
      error: (err) => {
        // Rollback on error
        this.progressMap.update((map) => ({ ...map, [topicId]: previousStatus }));
        this.updatingTopicId.set(null);
        this.errorMessage.set(err?.error?.message || 'Failed to update topic status.');
      },
    });
  }

  onTogglePyq(topicId: string): void {
    const currentPyq = this.isTopicPyqDone(topicId);
    const newPyq = !currentPyq;

    // Optimistic UI mutation
    this.pyqMap.update((map) => ({ ...map, [topicId]: newPyq }));

    this.progressService.toggleTopicPyq(topicId).subscribe({
      next: (res) => {
        this.pyqMap.update((map) => ({ ...map, [topicId]: res.pyqDone }));
      },
      error: (err) => {
        // Rollback on error
        this.pyqMap.update((map) => ({ ...map, [topicId]: currentPyq }));
        this.errorMessage.set(err?.error?.message || 'Failed to update PYQ status.');
      },
    });
  }

  onLogout(): void {
    this.authService.logout('/login');
  }
}
