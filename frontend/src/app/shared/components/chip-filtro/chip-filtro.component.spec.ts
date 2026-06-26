import { describe, it, expect, beforeEach, vi } from "vitest";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { ChipFiltroComponent } from "./chip-filtro.component";

describe("ChipFiltroComponent", () => {
  let component: ChipFiltroComponent;
  let fixture: ComponentFixture<ChipFiltroComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChipFiltroComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ChipFiltroComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  it("should have default empty label", () => {
    expect(component.label).toBe("");
  });

  it("should have default empty iconClass", () => {
    expect(component.iconClass).toBe("");
  });

  it("should default active to false", () => {
    expect(component.active).toBe(false);
  });

  it("should accept label via @Input", () => {
    component.label = "Cães";
    expect(component.label).toBe("Cães");
  });

  it("should accept active state via @Input", () => {
    component.active = true;
    expect(component.active).toBe(true);
  });

  it("should have chipClick EventEmitter defined", () => {
    expect(component.chipClick).toBeDefined();
  });

  it("should emit chipClick when triggered", () => {
    const emitSpy = vi.spyOn(component.chipClick, "emit");
    component.chipClick.emit();
    expect(emitSpy).toHaveBeenCalledTimes(1);
  });
});
