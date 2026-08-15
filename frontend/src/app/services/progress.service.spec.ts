import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ProgressService } from './progress.service';
import {
  OverallProgressResponse,
  SubjectProgressResponse,
  TopicProgressResponse,
} from '../models/progress.models';
import { environment } from '../../environments/environment';

describe('ProgressService', () => {
  let service: ProgressService;
  let httpMock: HttpTestingController;
  const baseUrl = `${environment.apiUrl}/progress`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ProgressService],
    });
    service = TestBed.inject(ProgressService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch overall progress summary (getOverallProgress)', () => {
    const mockOverall: OverallProgressResponse = {
      totalTopics: 35,
      completedTopics: 12,
      inProgressTopics: 5,
      notStartedTopics: 18,
      completionPercentage: 34,
      prelimsPercentage: 42,
      mainsPercentage: 28,
      optionalPercentage: 0,
    };

    service.getOverallProgress().subscribe((res) => {
      expect(res).toEqual(mockOverall);
      expect(res.completionPercentage).toBe(34);
    });

    const req = httpMock.expectOne(baseUrl);
    expect(req.request.method).toBe('GET');
    req.flush(mockOverall);
  });

  it('should fetch topic progress (getTopicProgress)', () => {
    const topicId = 'c0000000-0000-0000-0000-000000000001';
    const mockTopicProgress: TopicProgressResponse = {
      topicId,
      topicCode: 'PGS1_POLITY',
      topicName: 'Indian Polity and Governance',
      status: 'COMPLETED',
      subjectId: 'b0000000-0000-0000-0000-000000000001',
      subjectCode: 'PRELIMS_GS1',
      subjectName: 'General Studies Paper I',
    };

    service.getTopicProgress(topicId).subscribe((res) => {
      expect(res.status).toBe('COMPLETED');
      expect(res.topicCode).toBe('PGS1_POLITY');
    });

    const req = httpMock.expectOne(`${baseUrl}/topics/${topicId}`);
    expect(req.request.method).toBe('GET');
    req.flush(mockTopicProgress);
  });

  it('should update topic progress (updateTopicProgress)', () => {
    const topicId = 'c0000000-0000-0000-0000-000000000001';
    const mockUpdated: TopicProgressResponse = {
      topicId,
      topicCode: 'PGS1_POLITY',
      topicName: 'Indian Polity and Governance',
      status: 'IN_PROGRESS',
      subjectId: 'b0000000-0000-0000-0000-000000000001',
      subjectCode: 'PRELIMS_GS1',
      subjectName: 'General Studies Paper I',
    };

    service.updateTopicProgress(topicId, 'IN_PROGRESS').subscribe((res) => {
      expect(res.status).toBe('IN_PROGRESS');
    });

    const req = httpMock.expectOne(`${baseUrl}/topics/${topicId}`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual({ status: 'IN_PROGRESS' });
    req.flush(mockUpdated);
  });

  it('should fetch subject progress (getSubjectProgress)', () => {
    const subjectId = 'b0000000-0000-0000-0000-000000000001';
    const mockSubject: SubjectProgressResponse = {
      subjectId,
      subjectCode: 'PRELIMS_GS1',
      subjectName: 'General Studies Paper I',
      totalTopics: 10,
      completedTopics: 5,
      inProgressTopics: 2,
      notStartedTopics: 3,
      completionPercentage: 50,
    };

    service.getSubjectProgress(subjectId).subscribe((res) => {
      expect(res.completionPercentage).toBe(50);
      expect(res.completedTopics).toBe(5);
    });

    const req = httpMock.expectOne(`${baseUrl}/subjects/${subjectId}`);
    expect(req.request.method).toBe('GET');
    req.flush(mockSubject);
  });
});
