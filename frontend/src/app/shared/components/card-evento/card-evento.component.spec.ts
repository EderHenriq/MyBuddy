import { describe, it, expect, beforeEach, vi } from "vitest";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { NO_ERRORS_SCHEMA } from "@angular/core";
import { CardEventoComponent } from "./card-evento.component";

describe("CardEventoComponent", () => {
  let component: CardEventoComponent;
  let fixture: ComponentFixture<CardEventoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CardEventoComponent],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();

    fixture = TestBed.createComponent(CardEventoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  it("should default isFavorite to false", () => {
    expect(component.isFavorite).toBe(false);
  });

  it("should accept imageUrl input", () => {
    component.imageUrl = "https://example.com/image.jpg";
    expect(component.imageUrl).toBe("https://example.com/image.jpg");
  });

  it("should accept badgeText input", () => {
    component.badgeText = "Gratuito";
    expect(component.badgeText).toBe("Gratuito");
  });

  it("should accept title input", () => {
    component.title = "Feira de Adoção";
    expect(component.title).toBe("Feira de Adoção");
  });

  it("should accept dateStr and timeStr inputs", () => {
    component.dateStr = "2026-07-01";
    component.timeStr = "10:00";
    expect(component.dateStr).toBe("2026-07-01");
    expect(component.timeStr).toBe("10:00");
  });

  it("should accept locationStr and organizerStr inputs", () => {
    component.locationStr = "Parque da Cidade";
    component.organizerStr = "ONG Patinhas";
    expect(component.locationStr).toBe("Parque da Cidade");
    expect(component.organizerStr).toBe("ONG Patinhas");
  });

  it("should accept description input", () => {
    component.description = "Venha adotar um amigo!";
    expect(component.description).toBe("Venha adotar um amigo!");
  });

  it("should accept isFavorite = true", () => {
    component.isFavorite = true;
    expect(component.isFavorite).toBe(true);
  });

  it("should have detailsClick EventEmitter defined", () => {
    expect(component.detailsClick).toBeDefined();
  });

  it("should have favoriteClick EventEmitter defined", () => {
    expect(component.favoriteClick).toBeDefined();
  });

  it("should emit detailsClick when triggered", () => {
    const emitSpy = vi.spyOn(component.detailsClick, "emit");
    component.detailsClick.emit();
    expect(emitSpy).toHaveBeenCalledTimes(1);
  });

  it("should emit favoriteClick when triggered", () => {
    const emitSpy = vi.spyOn(component.favoriteClick, "emit");
    component.favoriteClick.emit();
    expect(emitSpy).toHaveBeenCalledTimes(1);
  });
});
