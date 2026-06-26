import { describe, it, expect, beforeEach } from "vitest";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { CardAvaliacaoComponent } from "./card-avaliacao.component";

describe("CardAvaliacaoComponent", () => {
  let component: CardAvaliacaoComponent;
  let fixture: ComponentFixture<CardAvaliacaoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CardAvaliacaoComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(CardAvaliacaoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  it("should have default avaliacao of 5", () => {
    expect(component.avaliacao).toBe(5);
  });

  it("should return 5 estrelas all true for avaliacao = 5", () => {
    component.avaliacao = 5;
    expect(component.estrelas).toEqual([true, true, true, true, true]);
  });

  it("should return correct pattern for avaliacao = 3", () => {
    component.avaliacao = 3;
    expect(component.estrelas).toEqual([true, true, true, false, false]);
  });

  it("should return all false for avaliacao = 0", () => {
    component.avaliacao = 0;
    expect(component.estrelas).toEqual([false, false, false, false, false]);
  });

  it("should return first letter uppercase for inicialNome", () => {
    component.nomeCliente = "Ana Silva";
    expect(component.inicialNome).toBe("A");
  });

  it("should return U for empty nomeCliente", () => {
    component.nomeCliente = "";
    expect(component.inicialNome).toBe("U");
  });

  it("should accept comentario and data inputs", () => {
    component.comentario = "Ótimo produto!";
    component.data = "2026-06-01";
    expect(component.comentario).toBe("Ótimo produto!");
    expect(component.data).toBe("2026-06-01");
  });
});
