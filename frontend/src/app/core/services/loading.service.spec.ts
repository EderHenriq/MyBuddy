import { describe, it, expect, beforeEach } from 'vitest';
import { TestBed } from '@angular/core/testing';
import { LoadingService } from './loading.service';

describe('LoadingService', () => {
  let service: LoadingService;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [LoadingService] });
    service = TestBed.inject(LoadingService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should start with loading false', () => {
    expect(service.isLoading()).toBe(false);
  });

  it('should set loading true on first show()', () => {
    service.show();
    expect(service.isLoading()).toBe(true);
  });

  it('should keep loading true on multiple show() calls', () => {
    service.show();
    service.show();
    expect(service.isLoading()).toBe(true);
  });

  it('should hide loading when all requests complete', () => {
    service.show();
    service.show();
    service.hide();
    expect(service.isLoading()).toBe(true);
    service.hide();
    expect(service.isLoading()).toBe(false);
  });

  it('should not go negative on extra hide() calls', () => {
    service.hide();
    service.hide();
    expect(service.isLoading()).toBe(false);
  });

  it('should cycle correctly: show → hide → show → hide', () => {
    service.show();
    expect(service.isLoading()).toBe(true);
    service.hide();
    expect(service.isLoading()).toBe(false);
    service.show();
    expect(service.isLoading()).toBe(true);
    service.hide();
    expect(service.isLoading()).toBe(false);
  });
});
