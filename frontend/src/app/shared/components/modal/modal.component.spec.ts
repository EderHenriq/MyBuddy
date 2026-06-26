import { describe, it, expect, beforeEach, vi } from "vitest";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { NO_ERRORS_SCHEMA } from "@angular/core";
import { ModalComponent } from "./modal.component";

describe("ModalComponent", () => {
  let component: ModalComponent;
  let fixture: ComponentFixture<ModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModalComponent],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();

    fixture = TestBed.createComponent(ModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  it("should have default input values", () => {
    expect(component.visible).toBe(false);
    expect(component.title).toBe("");
    expect(component.width).toBe("450px");
    expect(component.showFooter).toBe(true);
    expect(component.confirmLabel).toBe("Confirmar");
    expect(component.cancelLabel).toBe("Cancelar");
    expect(component.confirmSeverity).toBe("primary");
    expect(component.loading).toBe(false);
    expect(component.backdropBlur).toBe(false);
  });

  it("should emit visibleChange(false) and cancelled on onHide()", () => {
    const visibleSpy = vi.spyOn(component.visibleChange, "emit");
    const cancelledSpy = vi.spyOn(component.cancelled, "emit");

    component.onHide();

    expect(visibleSpy).toHaveBeenCalledWith(false);
    expect(cancelledSpy).toHaveBeenCalled();
  });

  it("should emit confirm on onConfirm()", () => {
    const confirmSpy = vi.spyOn(component.confirm, "emit");
    component.onConfirm();
    expect(confirmSpy).toHaveBeenCalled();
  });

  it("should emit visibleChange(false) and cancelled on onCancel()", () => {
    const visibleSpy = vi.spyOn(component.visibleChange, "emit");
    const cancelledSpy = vi.spyOn(component.cancelled, "emit");

    component.onCancel();

    expect(visibleSpy).toHaveBeenCalledWith(false);
    expect(cancelledSpy).toHaveBeenCalled();
  });

  it("should accept danger confirmSeverity", () => {
    component.confirmSeverity = "danger";
    expect(component.confirmSeverity).toBe("danger");
  });
});
