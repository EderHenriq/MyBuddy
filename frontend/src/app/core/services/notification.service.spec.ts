import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { TestBed } from '@angular/core/testing';
import { NotificationService } from './notification.service';
import { SessionService } from './session.service';
import { Role } from '../models/role.model';

describe('NotificationService', () => {
  let service: NotificationService;
  let sessionService: SessionService;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [NotificationService, SessionService],
    });
    sessionService = TestBed.inject(SessionService);
    service = TestBed.inject(NotificationService);
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should start with empty notifications when no role is set', done => {
    service.notificacoes$.subscribe(notifs => {
      expect(notifs).toEqual([]);
      done();
    });
  });

  it('should load admin notifications when role is ADMIN', done => {
    sessionService.setRole(Role.ADMIN);
    service.notificacoes$.subscribe(notifs => {
      if (notifs.length > 0) {
        expect(notifs.length).toBeGreaterThan(0);
        expect(notifs.some(n => n.tipo === 'warning' || n.tipo === 'error' || n.tipo === 'info')).toBe(true);
        done();
      }
    });
  });

  it('should load ONG notifications when role is ONG', done => {
    sessionService.setRole(Role.ONG);
    service.notificacoes$.subscribe(notifs => {
      if (notifs.length > 0) {
        expect(notifs.length).toBeGreaterThan(0);
        done();
      }
    });
  });

  it('should load PETSHOP notifications when role is PETSHOP', done => {
    sessionService.setRole(Role.PETSHOP);
    service.notificacoes$.subscribe(notifs => {
      if (notifs.length > 0) {
        expect(notifs.length).toBeGreaterThan(0);
        done();
      }
    });
  });

  it('should load USER notifications when role is USER', done => {
    sessionService.setRole(Role.USER);
    service.notificacoes$.subscribe(notifs => {
      if (notifs.length > 0) {
        expect(notifs.length).toBeGreaterThan(0);
        done();
      }
    });
  });

  it('should mark single notification as read', done => {
    sessionService.setRole(Role.ADMIN);

    let firstPass = true;
    service.notificacoes$.subscribe(notifs => {
      if (notifs.length > 0 && firstPass) {
        firstPass = false;
        const unreadId = notifs.find(n => !n.lida)?.id;
        if (unreadId) {
          service.marcarComoLida(unreadId);
        }
      } else if (notifs.length > 0 && !firstPass) {
        const targetNotif = notifs.find(n => n.lida);
        expect(targetNotif).toBeDefined();
        done();
      }
    });
  });

  it('should mark all notifications as read', done => {
    sessionService.setRole(Role.ONG);

    let called = false;
    service.notificacoes$.subscribe(notifs => {
      if (notifs.length > 0 && !called) {
        called = true;
        service.marcarTodasComoLidas();
      } else if (notifs.length > 0 && called) {
        expect(notifs.every(n => n.lida)).toBe(true);
        done();
      }
    });
  });

  it('should return count of unread notifications via buscarContagemNaoLidas()', done => {
    sessionService.setRole(Role.ADMIN);

    service.notificacoes$.subscribe(notifs => {
      if (notifs.length > 0) {
        service.buscarContagemNaoLidas().subscribe(count => {
          const expectedCount = notifs.filter(n => !n.lida).length;
          expect(count).toBe(expectedCount);
          done();
        });
      }
    });
  });

  it('should clear notifications when role is set to null', done => {
    sessionService.setRole(Role.ADMIN);

    let hasData = false;
    service.notificacoes$.subscribe(notifs => {
      if (notifs.length > 0 && !hasData) {
        hasData = true;
        sessionService.setRole(null);
      } else if (hasData && notifs.length === 0) {
        expect(notifs).toEqual([]);
        done();
      }
    });
  });
});
