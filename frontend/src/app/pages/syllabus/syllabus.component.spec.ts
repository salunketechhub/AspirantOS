import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SyllabusComponent } from './syllabus.component';
import { SyllabusService } from '../../services/syllabus.service';
import { AuthService } from '../../core/auth/auth.service';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { ExamResponse, OptionalSubjectResponse, SubjectResponse, TopicResponse } from '../../models/syllabus.models';
import { signal } from '@angular/core';

describe('SyllabusComponent', () => {
  let component: SyllabusComponent;
  let fixture: ComponentFixture<SyllabusComponent>;
  let syllabusServiceMock: jasmine.SpyObj<SyllabusService>;
  let authServiceMock: any;

  const mockExam: ExamResponse = {
    id: 'a0000000-0000-0000-0000-000000000001',
    code: 'PRELIMS',
    name: 'UPSC Civil Services (Preliminary) Examination',
    description: 'Prelims description',
    stage: 'PRELIMS',
    displayOrder: 1,
    subjectCount: 2,
  };

  const mockMainsExam: ExamResponse = {
    id: 'a0000000-0000-0000-0000-000000000002',
    code: 'MAINS',
    name: 'UPSC Civil Services (Main) Examination',
    description: 'Mains description',
    stage: 'MAINS',
    displayOrder: 2,
    subjectCount: 5,
  };

  const mockSubject: SubjectResponse = {
    id: 'b0000000-0000-0000-0000-000000000001',
    examId: 'a0000000-0000-0000-0000-000000000001',
    examCode: 'PRELIMS',
    examName: 'UPSC Prelims',
    code: 'PRELIMS_GS1',
    name: 'General Studies Paper I',
    description: 'GS Paper 1',
    paper: 'Paper I',
    displayOrder: 1,
    topicCount: 1,
  };

  const mockTopic: TopicResponse = {
    id: 'c0000000-0000-0000-0000-000000000001',
    subjectId: 'b0000000-0000-0000-0000-000000000001',
    subjectCode: 'PRELIMS_GS1',
    subjectName: 'General Studies Paper I',
    parentTopicId: null,
    code: 'PGS1_POLITY',
    name: 'Indian Polity & Governance',
    description: 'Polity details',
    level: 1,
    displayOrder: 1,
    subtopics: [
      {
        id: 'c0000000-0000-0000-0000-000000000002',
        subjectId: 'b0000000-0000-0000-0000-000000000001',
        subjectCode: 'PRELIMS_GS1',
        subjectName: 'General Studies Paper I',
        parentTopicId: 'c0000000-0000-0000-0000-000000000001',
        code: 'PGS1_POLITY_CONST',
        name: 'Constitutional Framework',
        description: 'Constitution',
        level: 2,
        displayOrder: 1,
        subtopics: [],
        subtopicCount: 0,
      },
    ],
    subtopicCount: 1,
  };

  const mockOptional: OptionalSubjectResponse = {
    id: 'd0000000-0000-0000-0000-000000000001',
    code: 'OPT_PUB_AD',
    name: 'Public Administration',
    description: 'Administrative theory',
    displayOrder: 1,
  };

  beforeEach(async () => {
    syllabusServiceMock = jasmine.createSpyObj('SyllabusService', [
      'getExams',
      'getSubjectsByExam',
      'getTopicsBySubject',
      'getOptionals',
    ]);

    syllabusServiceMock.getExams.and.returnValue(of([mockExam, mockMainsExam]));
    syllabusServiceMock.getSubjectsByExam.and.returnValue(of([mockSubject]));
    syllabusServiceMock.getTopicsBySubject.and.returnValue(of([mockTopic]));
    syllabusServiceMock.getOptionals.and.returnValue(of([mockOptional]));

    authServiceMock = {
      userName: signal('Aarav Sharma'),
      isAuthenticated: signal(true),
      logout: jasmine.createSpy('logout'),
    };

    await TestBed.configureTestingModule({
      imports: [SyllabusComponent],
      providers: [
        { provide: SyllabusService, useValue: syllabusServiceMock },
        { provide: AuthService, useValue: authServiceMock },
        provideRouter([]),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(SyllabusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create SyllabusComponent', () => {
    expect(component).toBeTruthy();
  });

  it('should load exams and select Prelims by default', () => {
    expect(syllabusServiceMock.getExams).toHaveBeenCalled();
    expect(component.selectedExam()?.code).toBe('PRELIMS');
    expect(component.selectedSubject()?.code).toBe('PRELIMS_GS1');
    expect(component.topics().length).toBe(1);
  });

  it('should toggle topic expansion', () => {
    const topicId = 'c0000000-0000-0000-0000-000000000001';
    expect(component.isExpanded(topicId)).toBeTrue();

    component.toggleTopic(topicId);
    expect(component.isExpanded(topicId)).toBeFalse();

    component.toggleTopic(topicId);
    expect(component.isExpanded(topicId)).toBeTrue();
  });

  it('should filter topics matching search query', () => {
    component.searchQuery.set('Constitutional');
    const filtered = component.filteredTopics();
    expect(filtered.length).toBe(1);
    expect(filtered[0].subtopics[0].name).toContain('Constitutional Framework');
  });

  it('should switch to Optionals tab', () => {
    component.selectTab('OPTIONALS');
    expect(component.activeTab()).toBe('OPTIONALS');
    expect(component.filteredOptionals().length).toBe(1);
    expect(component.filteredOptionals()[0].code).toBe('OPT_PUB_AD');
  });
});
