import { Injectable } from '@angular/core';
import { Observable, delay, of } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UploadService {
  constructor() {}

  /**
   * Mocked method to upload a single image
   */
  uploadImage(file: File): Observable<string> {
    console.log(`[UploadService Mock] Uploading single image: ${file.name}`);
    // Return a mocked image URL after a short delay
    return of('https://images.unsplash.com/photo-1548199973-03cce0bbc87b?auto=format&fit=crop&q=80&w=600').pipe(delay(1000));
  }

  /**
   * Mocked method to upload multiple images
   */
  uploadImages(files: File[]): Observable<string[]> {
    console.log(`[UploadService Mock] Uploading ${files.length} images`);
    // Return an array of mocked image URLs after a short delay
    const mockedUrls = files.map(
      (_, index) => `https://images.unsplash.com/photo-1574158622682-e40e69881006?auto=format&fit=crop&q=80&w=600&mock=${index}`,
    );
    return of(mockedUrls).pipe(delay(1500));
  }
}
