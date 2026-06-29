import { describe, it, expect, beforeEach, vi } from "vitest";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { CardProdutoComponent } from "./card-produto.component";

describe("CardProdutoComponent", () => {
  let component: CardProdutoComponent;
  let fixture: ComponentFixture<CardProdutoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CardProdutoComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(CardProdutoComponent);
    component = fixture.componentInstance;
    component.preco = 99.9;
    component.nomeLoja = "Petz";
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  it("should default quantity to 0", () => {
    expect(component.quantidade).toBe(0);
  });

  it("should default favorito to false", () => {
    expect(component.favorito).toBe(false);
  });

  it("should increment quantity and emit on aoClicarAdicionar()", () => {
    const emitSpy = vi.spyOn(component.adicionarAoCarrinho, "emit");
    const event = new MouseEvent("click");

    component.aoClicarAdicionar(event);

    expect(component.quantidade).toBe(1);
    expect(emitSpy).toHaveBeenCalledWith(1);
  });

  it("should increment quantity twice", () => {
    const event = new MouseEvent("click");
    component.aoClicarAdicionar(event);
    component.aoClicarAdicionar(event);
    expect(component.quantidade).toBe(2);
  });

  it("should decrement quantity on aoDiminuir() when > 0", () => {
    component.quantidade = 3;
    const emitSpy = vi.spyOn(component.adicionarAoCarrinho, "emit");
    const event = new MouseEvent("click");

    component.aoDiminuir(event);

    expect(component.quantidade).toBe(2);
    expect(emitSpy).toHaveBeenCalledWith(2);
  });

  it("should not decrement below 0 on aoDiminuir()", () => {
    component.quantidade = 0;
    const emitSpy = vi.spyOn(component.adicionarAoCarrinho, "emit");
    const event = new MouseEvent("click");

    component.aoDiminuir(event);

    expect(component.quantidade).toBe(0);
    expect(emitSpy).not.toHaveBeenCalled();
  });

  it("should emit cliqueFavorito on aoClicarFavorito()", () => {
    const emitSpy = vi.spyOn(component.cliqueFavorito, "emit");
    const event = new MouseEvent("click");

    component.aoClicarFavorito(event);

    expect(emitSpy).toHaveBeenCalledWith(event);
  });

  it("should emit cliqueCard when configured", () => {
    expect(component.cliqueCard).toBeDefined();
  });

  it("should accept precoAntigo input", () => {
    component.precoAntigo = 120.0;
    expect(component.precoAntigo).toBe(120.0);
  });
});
