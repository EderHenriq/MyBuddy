import { Directive, EventEmitter, HostListener, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { Subject, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

@Directive({
  selector: '[appDebounce]',
  standalone: true
})
export class DebounceDirective implements OnInit, OnDestroy {
  @Input() debounceTime = 300;
  @Output() debounceOutput = new EventEmitter<string>();

  private subject = new Subject<string>();
  private subscription!: Subscription;

  ngOnInit() {
    this.subscription = this.subject.pipe(
      debounceTime(this.debounceTime),
      distinctUntilChanged()
    ).subscribe(value => {
      this.debounceOutput.emit(value);
    });
  }

  @HostListener('input', ['$event'])
  onInput(event: Event) {
    const target = event.target as HTMLInputElement;
    if (target) {
      this.subject.next(target.value);
    }
  }

  ngOnDestroy() {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }
}
