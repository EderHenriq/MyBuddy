import { Component } from '@angular/core';
import { NgClass } from '@angular/common';
import { toSignal } from '@angular/core/rxjs-interop';
import { inject } from '@angular/core';
import { ToastService, ToastMessage } from '../../services/toast/toast';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [NgClass],
  templateUrl: './toast.html',
  styleUrl: './toast.scss',
})
export class Toast {
  private ToastService = inject(ToastService);

  toasts = toSignal(this.ToastService.toasts$, { initialValue: [] as ToastMessage[] });

  removeToast(id: string) {
    this.ToastService.remove(id);
  }

  getIcon(type: string): string {
    const icons: Record<string, string> = {
      sucess: 'check_circle',
      error: 'error',
      warning: 'warning',
      info: 'info',
    };

    return icons[type] ?? 'info';
  }
}
