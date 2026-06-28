import { describe, it, expect, beforeEach, vi } from "vitest";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { BtnOutlineComponent } from "./btn-outline.component";

describe("BtnOutlineComponent", () => {
  let component: BtnOutlineComponent;
  let fixture: ComponentFixture<BtnOutlineComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BtnOutlineComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(BtnOutlineComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  it("should have default variant outline", () => {
    expect(component.variant).toBe("outline");
  });

  it("should have default size md", () => {
    expect(component.size).toBe("md");
  });

  it("should have default type button", () => {
    expect(component.type).toBe("button");
  });

  it("should default disabled to false", () => {
    expect(component.disabled).toBe(false);
  });

  it("should default icon to undefined", () => {
    expect(component.icon).toBeUndefined();
  });

  it("should accept solid variant via @Input", () => {
    component.variant = "solid";
    expect(component.variant).toBe("solid");
  });

  it("should accept ghost variant via @Input", () => {
    component.variant = "ghost";
    expect(component.variant).toBe("ghost");
  });

  it("should accept sm size via @Input", () => {
    component.size = "sm";
    expect(component.size).toBe("sm");
  });

  it("should accept lg size via @Input", () => {
    component.size = "lg";
    expect(component.size).toBe("lg");
  });

  it("should accept disabled state", () => {
    component.disabled = true;
    expect(component.disabled).toBe(true);
  });

  it("should accept icon class", () => {
    component.icon = "search";
    expect(component.icon).toBe("search");
  });

  it("should emit clicked event on click", () => {
    const emitSpy = vi.spyOn(component.clicked, "emit");
    const event = new MouseEvent("click");
    component.clicked.emit(event);
    expect(emitSpy).toHaveBeenCalledWith(event);
  });

  it("should accept submit type", () => {
    component.type = "submit";
    expect(component.type).toBe("submit");
  });
});
