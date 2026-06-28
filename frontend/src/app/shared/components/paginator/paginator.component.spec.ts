import { describe, it, expect, beforeEach, vi } from "vitest";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { PaginatorComponent } from "./paginator.component";

describe("PaginatorComponent", () => {
  let component: PaginatorComponent;
  let fixture: ComponentFixture<PaginatorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PaginatorComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(PaginatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  it("should default to page 1 of 1", () => {
    expect(component.currentPage).toBe(1);
    expect(component.totalPages).toBe(1);
  });

  // ── pages getter ─────────────────────────────────────────────────────────

  it("should return all pages when totalPages <= 5", () => {
    component.totalPages = 4;
    component.currentPage = 2;
    expect(component.pages).toEqual([1, 2, 3, 4]);
  });

  it("should truncate end with ellipsis for large page sets at start", () => {
    component.totalPages = 10;
    component.currentPage = 1;
    const pages = component.pages;
    expect(pages).toContain("...");
    expect(pages[0]).toBe(1);
    expect(pages[pages.length - 1]).toBe(10);
  });

  it("should truncate start with ellipsis near last pages", () => {
    component.totalPages = 10;
    component.currentPage = 9;
    const pages = component.pages;
    expect(pages).toContain("...");
    expect(pages[0]).toBe(1);
    expect(pages[pages.length - 1]).toBe(10);
  });

  it("should show ellipsis on both sides for middle pages", () => {
    component.totalPages = 10;
    component.currentPage = 5;
    const pages = component.pages;
    expect(pages.filter((p) => p === "...").length).toBe(2);
    expect(pages).toContain(4);
    expect(pages).toContain(5);
    expect(pages).toContain(6);
  });

  // ── goToPage ─────────────────────────────────────────────────────────────

  it("should emit pageChange on goToPage with valid page", () => {
    component.totalPages = 5;
    component.currentPage = 2;
    const emitSpy = vi.spyOn(component.pageChange, "emit");

    component.goToPage(4);
    expect(emitSpy).toHaveBeenCalledWith(4);
  });

  it("should not emit pageChange for current page", () => {
    component.totalPages = 5;
    component.currentPage = 3;
    const emitSpy = vi.spyOn(component.pageChange, "emit");

    component.goToPage(3);
    expect(emitSpy).not.toHaveBeenCalled();
  });

  it("should not emit for string page", () => {
    const emitSpy = vi.spyOn(component.pageChange, "emit");
    component.goToPage("...");
    expect(emitSpy).not.toHaveBeenCalled();
  });

  it("should not emit for page out of range", () => {
    component.totalPages = 5;
    component.currentPage = 1;
    const emitSpy = vi.spyOn(component.pageChange, "emit");

    component.goToPage(0);
    component.goToPage(6);
    expect(emitSpy).not.toHaveBeenCalled();
  });

  // ── previous / next ──────────────────────────────────────────────────────

  it("should emit previous page on previous()", () => {
    component.totalPages = 5;
    component.currentPage = 3;
    const emitSpy = vi.spyOn(component.pageChange, "emit");

    component.previous();
    expect(emitSpy).toHaveBeenCalledWith(2);
  });

  it("should not emit on previous() when on first page", () => {
    component.currentPage = 1;
    const emitSpy = vi.spyOn(component.pageChange, "emit");

    component.previous();
    expect(emitSpy).not.toHaveBeenCalled();
  });

  it("should emit next page on next()", () => {
    component.totalPages = 5;
    component.currentPage = 2;
    const emitSpy = vi.spyOn(component.pageChange, "emit");

    component.next();
    expect(emitSpy).toHaveBeenCalledWith(3);
  });

  it("should not emit on next() when on last page", () => {
    component.totalPages = 3;
    component.currentPage = 3;
    const emitSpy = vi.spyOn(component.pageChange, "emit");

    component.next();
    expect(emitSpy).not.toHaveBeenCalled();
  });
});
