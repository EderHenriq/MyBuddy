import { describe, it, expect, beforeEach, afterEach } from "vitest";
import { TestBed } from "@angular/core/testing";
import { SessionService } from "./session.service";
import { Role } from "../models/role.model";

describe("SessionService", () => {
  let service: SessionService;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({ providers: [SessionService] });
    service = TestBed.inject(SessionService);
  });

  afterEach(() => {
    localStorage.clear();
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });

  it("should start with null role when localStorage is empty", () => {
    expect(service.getCurrentRole()).toBeNull();
  });

  it("should set and get a role", () => {
    service.setRole(Role.USER);
    expect(service.getCurrentRole()).toBe(Role.USER);
  });

  it("should persist role in localStorage", () => {
    service.setRole(Role.ADMIN);
    expect(localStorage.getItem("mockUserRole")).toBe(Role.ADMIN);
  });

  it("should clear role from localStorage when set to null", () => {
    service.setRole(Role.ONG);
    service.setRole(null);
    expect(localStorage.getItem("mockUserRole")).toBeNull();
    expect(service.getCurrentRole()).toBeNull();
  });

  it("should emit role changes via userRole$", (done) => {
    const roles: (Role | null)[] = [];
    const subscription = service.userRole$.subscribe((r) => roles.push(r));

    service.setRole(Role.PETSHOP);
    service.setRole(null);

    subscription.unsubscribe();
    expect(roles).toContain(Role.PETSHOP);
    expect(roles).toContain(null);
    done();
  });

  it("should restore role from localStorage on construction", () => {
    localStorage.setItem("mockUserRole", Role.ONG);
    TestBed.resetTestingModule();
    TestBed.configureTestingModule({ providers: [SessionService] });
    const freshService = TestBed.inject(SessionService);
    expect(freshService.getCurrentRole()).toBe(Role.ONG);
  });

  it("should update to different roles sequentially", () => {
    service.setRole(Role.USER);
    expect(service.getCurrentRole()).toBe(Role.USER);
    service.setRole(Role.ADMIN);
    expect(service.getCurrentRole()).toBe(Role.ADMIN);
    service.setRole(Role.PETSHOP);
    expect(service.getCurrentRole()).toBe(Role.PETSHOP);
  });
});
