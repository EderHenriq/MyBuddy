import { describe, it, expect, beforeEach, vi } from "vitest";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { SearchBarComponent } from "./search-bar.component";

describe("SearchBarComponent", () => {
  let component: SearchBarComponent;
  let fixture: ComponentFixture<SearchBarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SearchBarComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(SearchBarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  it("should have default placeholder", () => {
    expect(component.placeholder).toBe("O que você procura?");
  });

  it("should accept custom placeholder via @Input", () => {
    component.placeholder = "Buscar pets";
    fixture.detectChanges();
    expect(component.placeholder).toBe("Buscar pets");
  });

  it("should start with empty searchTerm", () => {
    expect(component.searchTerm).toBe("");
  });

  it("should emit searchTriggered with current term on onSearch()", () => {
    const emitSpy = vi.spyOn(component.searchTriggered, "emit");
    component.searchTerm = "labrador";
    component.onSearch();
    expect(emitSpy).toHaveBeenCalledWith("labrador");
  });

  it("should emit empty string if searchTerm is empty", () => {
    const emitSpy = vi.spyOn(component.searchTriggered, "emit");
    component.searchTerm = "";
    component.onSearch();
    expect(emitSpy).toHaveBeenCalledWith("");
  });
});
