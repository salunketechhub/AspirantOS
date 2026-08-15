import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { SyllabusService } from '../../services/syllabus.service';
import { AuthService } from '../../core/auth/auth.service';
import {
  ExamResponse,
  OptionalSubjectResponse,
  SubjectResponse,
  TopicResponse,
} from '../../models/syllabus.models';

@Component({
  selector: 'app-syllabus',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './syllabus.component.html',
  styleUrl: './syllabus.component.css',
})
export class SyllabusComponent implements OnInit {
  private readonly syllabusService = inject(SyllabusService);
  readonly authService = inject(AuthService);

  // Core data signals
  readonly exams = signal<ExamResponse[]>([]);
  readonly selectedExam = signal<ExamResponse | null>(null);
  readonly subjects = signal<SubjectResponse[]>([]);
  readonly selectedSubject = signal<SubjectResponse | null>(null);
  readonly topics = signal<TopicResponse[]>([]);
  readonly optionals = signal<OptionalSubjectResponse[]>([]);

  // Navigation & interaction signals
  readonly activeTab = signal<'PRELIMS' | 'MAINS' | 'OPTIONALS'>('PRELIMS');
  readonly searchQuery = signal<string>('');
  readonly expandedTopicIds = signal<Set<string>>(new Set());

  // Status signals
  readonly isLoading = signal<boolean>(false);
  readonly isTopicsLoading = signal<boolean>(false);
  readonly errorMessage = signal<string | null>(null);

  // Computed filtered topics based on search
  readonly filteredTopics = computed(() => {
    const query = this.searchQuery().trim().toLowerCase();
    const rawTopics = this.topics();

    if (!query) {
      return rawTopics;
    }

    return this.filterTopicList(rawTopics, query);
  });

  // Computed filtered optional subjects
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

  // Total topics count in currently selected subject
  readonly currentSubjectTopicCount = computed(() => {
    return this.countTopicsRecursively(this.topics());
  });

  ngOnInit(): void {
    this.loadInitialData();
  }

  loadInitialData(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.syllabusService.getExams().subscribe({
      next: (examsList) => {
        this.exams.set(examsList);
        this.isLoading.set(false);

        // Find Prelims or default to first exam
        const prelims = examsList.find((e) => e.code.toUpperCase() === 'PRELIMS') || examsList[0];
        if (prelims) {
          this.selectExam(prelims);
        }
      },
      error: (err) => {
        this.isLoading.set(false);
        this.errorMessage.set(
          err?.error?.message || err?.message || 'Failed to load exam stages. Please check backend connection.'
        );
      },
    });

    // Preload Optionals Catalogue
    this.syllabusService.getOptionals().subscribe({
      next: (opts) => this.optionals.set(opts),
      error: () => {},
    });
  }

  selectTab(tab: 'PRELIMS' | 'MAINS' | 'OPTIONALS'): void {
    this.activeTab.set(tab);
    this.searchQuery.set('');

    if (tab === 'OPTIONALS') {
      this.selectedExam.set(null);
      this.selectedSubject.set(null);
      this.topics.set([]);
      return;
    }

    const matchedExam = this.exams().find((e) => e.code.toUpperCase() === tab);
    if (matchedExam) {
      this.selectExam(matchedExam);
    }
  }

  selectExam(exam: ExamResponse): void {
    this.selectedExam.set(exam);
    this.activeTab.set(exam.stage === 'PRELIMS' ? 'PRELIMS' : 'MAINS');
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
      next: (topicTree) => {
        this.topics.set(topicTree);
        // By default expand top-level nodes
        const defaultExpanded = new Set<string>();
        topicTree.forEach((t) => defaultExpanded.add(t.id));
        this.expandedTopicIds.set(defaultExpanded);
        this.isTopicsLoading.set(false);
      },
      error: (err) => {
        this.isTopicsLoading.set(false);
        this.errorMessage.set(err?.error?.message || 'Failed to load topic hierarchy.');
      },
    });
  }

  toggleTopic(topicId: string): void {
    this.expandedTopicIds.update((currentSet) => {
      const next = new Set(currentSet);
      if (next.has(topicId)) {
        next.delete(topicId);
      } else {
        next.add(topicId);
      }
      return next;
    });
  }

  isExpanded(topicId: string): boolean {
    return this.expandedTopicIds().has(topicId);
  }

  expandAll(): void {
    const allIds = new Set<string>();
    const collectIds = (items: TopicResponse[]) => {
      for (const item of items) {
        allIds.add(item.id);
        if (item.subtopics && item.subtopics.length > 0) {
          collectIds(item.subtopics);
        }
      }
    };
    collectIds(this.topics());
    this.expandedTopicIds.set(allIds);
  }

  collapseAll(): void {
    this.expandedTopicIds.set(new Set());
  }

  onLogout(): void {
    this.authService.logout('/login');
  }

  // --- Helper filter functions ---

  private filterTopicList(list: TopicResponse[], query: string): TopicResponse[] {
    const result: TopicResponse[] = [];

    for (const topic of list) {
      const nameMatch = topic.name.toLowerCase().includes(query);
      const codeMatch = topic.code.toLowerCase().includes(query);
      const descMatch = topic.description ? topic.description.toLowerCase().includes(query) : false;

      const filteredSubtopics = topic.subtopics ? this.filterTopicList(topic.subtopics, query) : [];

      if (nameMatch || codeMatch || descMatch || filteredSubtopics.length > 0) {
        // Auto-expand matched parent
        this.expandedTopicIds.update((set) => {
          const next = new Set(set);
          next.add(topic.id);
          return next;
        });

        result.push({
          ...topic,
          subtopics: filteredSubtopics.length > 0 ? filteredSubtopics : topic.subtopics,
        });
      }
    }

    return result;
  }

  private countTopicsRecursively(list: TopicResponse[]): number {
    let count = 0;
    for (const t of list) {
      count += 1;
      if (t.subtopics && t.subtopics.length > 0) {
        count += this.countTopicsRecursively(t.subtopics);
      }
    }
    return count;
  }
}
