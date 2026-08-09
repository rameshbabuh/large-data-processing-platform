import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ProcessingJob {
  id: string;
  fileName: string;
  status: string;
  totalRecords: number;
  processedRecords: number;
  successfulRecords: number;
  failedRecords: number;
  createdAt: string;
}

export interface ProcessingError {
  id: string;
  processingJobId: string;
  rowNumber: number;
  rawData: string;
  errorMessage: string;
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class JobService {

  private readonly baseUrl = 'http://localhost:8080/api/jobs';

  constructor(private http: HttpClient) {}

  upload(file: File): Observable<ProcessingJob> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post<ProcessingJob>(
      `${this.baseUrl}/upload`,
      formData
    );
  }

  getAllJobs(): Observable<ProcessingJob[]> {
    return this.http.get<ProcessingJob[]>(this.baseUrl);
  }

  getErrors(jobId: string): Observable<ProcessingError[]> {
    return this.http.get<ProcessingError[]>(
      `${this.baseUrl}/${jobId}/errors`
    );
  }
}
