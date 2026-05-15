import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class LoadingService {
  private isLoadingSignal = signal<boolean>(false);
  public readonly isLoading = this.isLoadingSignal.asReadonly();
  
  private requestCount = 0;

  show(): void {
    this.requestCount++;
    if (this.requestCount === 1) {
      this.isLoadingSignal.set(true);
    }
  }

  hide(): void {
    if (this.requestCount > 0) {
      this.requestCount--;
    }
    if (this.requestCount === 0) {
      this.isLoadingSignal.set(false);
    }
  }
}
