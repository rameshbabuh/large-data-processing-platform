import {Component, OnDestroy, OnInit, signal} from '@angular/core';
import {JobService, ProcessingError, ProcessingJob, Transaction} from './services/job.service';
import {interval, Subscription} from 'rxjs';

@Component({
  selector: 'app-root',
  imports: [],
  templateUrl: './app.html',
  styleUrl: './app.css',
  standalone: true
})
export class App implements OnInit, OnDestroy {

  selectedFile: File | null = null;
  uploadedJob: ProcessingJob | null = null;
  jobs = signal<ProcessingJob[]>([]);

  selectedErrors = signal<ProcessingError[]>([]);

  selectedTransactions = signal<Transaction[]>([]);
  transactionPage = signal(0);
  transactionTotalPages = signal(0);
  selectedTransactionJobId = signal<string | null>(null);

  jobPage = signal(0);
  jobTotalPages = signal(0);

  errorPage = signal(0);
  errorTotalPages = signal(0);
  selectedErrorJobId = signal<string | null>(null);

  private pollingSubscription?: Subscription;

  constructor(private jobService: JobService) {}

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;

    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
    }
  }

  upload() {
    if (!this.selectedFile) {
      return;
    }

    this.jobService.upload(this.selectedFile)
      .subscribe(job => {
        this.uploadedJob = job;
        this.loadJobs(0);
      });
  }

  loadJobs(page: number = 0) {
    this.jobService.getAllJobs(page)
      .subscribe(response => {
        const jobs = response.content;
        this.jobs.set(jobs);
        this.jobPage.set(response.page);
        this.jobTotalPages.set(response.totalPages);

        if (this.uploadedJob) {
          const updatedJob = jobs.find(
            job => job.id === this.uploadedJob?.id
          );

          if (updatedJob) {
            this.uploadedJob = updatedJob;
          }
        }
      });
  }

  loadErrors(jobId: string, page: number = 0) {
    this.selectedTransactions.set([]);

    this.jobService.getErrors(jobId, page)
      .subscribe(response => {
        this.selectedErrors.set(response.content);
        this.errorPage.set(response.page);
        this.errorTotalPages.set(response.totalPages);
        this.selectedErrorJobId.set(jobId);
      });
  }

  ngOnInit() {
    this.loadJobs();

    this.pollingSubscription = interval(2000)
      .subscribe(() => {
        const hasActiveJob = this.jobs().some(job =>
          job.status === 'QUEUED' || job.status === 'PROCESSING'
        );

        if (hasActiveJob) {
          this.loadJobs(this.jobPage());
        }
      });
  }

  ngOnDestroy() {
    this.pollingSubscription?.unsubscribe();
  }

  getProgress(job: ProcessingJob): number | null {
    if (!job.totalRecords) {
      return null;
    }

    return Math.round(
      (job.processedRecords / job.totalRecords) * 100
    );
  }

  loadTransactions(jobId: string, page: number = 0) {
    this.selectedErrors.set([]); //reset

    this.jobService.getTransactions(jobId, page)
      .subscribe(response => {
        this.selectedTransactions.set(response.content);
        this.transactionPage.set(response.page);
        this.transactionTotalPages.set(response.totalPages);
        this.selectedTransactionJobId.set(jobId);
      });
  }

  getDuration(job: ProcessingJob): string {
    if (!job.startedAt || !job.completedAt) {
      return '-';
    }

    const start = new Date(job.startedAt).getTime();
    const end = new Date(job.completedAt).getTime();

    const seconds = Math.round((end - start) / 1000);

    return `${seconds}s`;
  }

  getThroughput(job: ProcessingJob): string {
    if (!job.startedAt || !job.completedAt || !job.totalRecords) {
      return '-';
    }

    const start = new Date(job.startedAt).getTime();
    const end = new Date(job.completedAt).getTime();

    const seconds = (end - start) / 1000;

    if (seconds <= 0) {
      return '-';
    }

    return Math.round(job.totalRecords / seconds).toLocaleString();
  }
}
