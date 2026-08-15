import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { SyllabusService } from './syllabus.service';
import { environment } from '../../environments/environment';
import { ExamResponse, SubjectResponse, TopicResponse } from '../models/syllabus.models';

describe('SyllabusService', () => {
  let service: SyllabusService;
  let httpMock: HttpTestingController;

  const mockExam: ExamResponse = {
    id: 'a0000000-0000-0000-0000-000000000001',
    code: 'PRELIMS',
    name: 'UPSC Prelims',
    description: 'Prelims description',
    stage: 'PRELIMS',
    displayOrder: 1,
    subjectCount: 2,
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
    topicCount: 5,
  };

  const mockTopic: TopicResponse = {
    id: 'c0000000-0000-0000-0000-000000000001',
    subjectId: 'b0000000-0000-0000-0000-000000000001',
    subjectCode: 'PRELIMS_GS1',
    subjectName: 'General Studies Paper I',
    parentTopicId: null,
    code: 'PGS1_POLITY',
    name: 'Indian Polity & Governance',
    description: 'Polity',
    level: 1,
    displayOrder: 1,
    subtopics: [],
    subtopicCount: 0,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        SyllabusService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    service = TestBed.inject(SyllabusService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch all exams', () => {
    service.getExams().subscribe((exams) => {
      expect(exams.length).toBe(1);
      expect(exams[0].code).toBe('PRELIMS');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/syllabus/exams`);
    expect(req.request.method).toBe('GET');
    req.flush([mockExam]);
  });

  it('should fetch subjects by exam ID', () => {
    service.getSubjectsByExam('a0000000-0000-0000-0000-000000000001').subscribe((subjects) => {
      expect(subjects.length).toBe(1);
      expect(subjects[0].code).toBe('PRELIMS_GS1');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/syllabus/exams/a0000000-0000-0000-0000-000000000001/subjects`);
    expect(req.request.method).toBe('GET');
    req.flush([mockSubject]);
  });

  it('should fetch hierarchical topics by subject ID', () => {
    service.getTopicsBySubject('b0000000-0000-0000-0000-000000000001').subscribe((topics) => {
      expect(topics.length).toBe(1);
      expect(topics[0].code).toBe('PGS1_POLITY');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/syllabus/subjects/b0000000-0000-0000-0000-000000000001/topics`);
    expect(req.request.method).toBe('GET');
    req.flush([mockTopic]);
  });

  it('should fetch optional subjects catalogue', () => {
    service.getOptionals().subscribe((opts) => {
      expect(opts.length).toBe(1);
      expect(opts[0].code).toBe('OPT_PSIR');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/syllabus/optionals`);
    expect(req.request.method).toBe('GET');
    req.flush([{ id: '1', code: 'OPT_PSIR', name: 'PSIR', description: '', displayOrder: 1 }]);
  });
});
