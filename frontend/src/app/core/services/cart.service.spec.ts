import { describe, it, expect, beforeEach } from "vitest";
import { TestBed } from "@angular/core/testing";
import { CartService, ItemCarrinho } from "./cart.service";

const mockItem: Omit<ItemCarrinho, "quantidade"> = {
  id: 1,
  nome: "Ração Premium",
  preco: 99.9,
  urlImagem: "/img/racao.jpg",
  lojaNome: "Petz",
};

describe("CartService", () => {
  let service: CartService;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [CartService] });
    service = TestBed.inject(CartService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });

  it("should start with empty cart", () => {
    expect(service.itensCarrinho()).toEqual([]);
    expect(service.totalItens()).toBe(0);
    expect(service.precoTotal()).toBe(0);
  });

  it("should start with drawer closed", () => {
    expect(service.gavetaAberta()).toBe(false);
  });

  it("should toggle drawer", () => {
    service.alternarGaveta();
    expect(service.gavetaAberta()).toBe(true);
    service.alternarGaveta();
    expect(service.gavetaAberta()).toBe(false);
  });

  it("should open and close drawer explicitly", () => {
    service.abrirGaveta();
    expect(service.gavetaAberta()).toBe(true);
    service.fecharGaveta();
    expect(service.gavetaAberta()).toBe(false);
  });

  it("should add item to cart with quantity 1", () => {
    service.adicionarAoCarrinho(mockItem);
    expect(service.itensCarrinho().length).toBe(1);
    expect(service.itensCarrinho()[0].quantidade).toBe(1);
    expect(service.totalItens()).toBe(1);
  });

  it("should increment quantity when adding existing item", () => {
    service.adicionarAoCarrinho(mockItem);
    service.adicionarAoCarrinho(mockItem);
    expect(service.itensCarrinho().length).toBe(1);
    expect(service.itensCarrinho()[0].quantidade).toBe(2);
    expect(service.totalItens()).toBe(2);
  });

  it("should add different items separately", () => {
    const item2 = { ...mockItem, id: 2, nome: "Coleira" };
    service.adicionarAoCarrinho(mockItem);
    service.adicionarAoCarrinho(item2);
    expect(service.itensCarrinho().length).toBe(2);
    expect(service.totalItens()).toBe(2);
  });

  it("should calculate total price correctly", () => {
    service.adicionarAoCarrinho(mockItem);
    service.adicionarAoCarrinho(mockItem);
    expect(service.precoTotal()).toBeCloseTo(199.8);
  });

  it("should remove item from cart", () => {
    service.adicionarAoCarrinho(mockItem);
    service.removerDoCarrinho(mockItem.id);
    expect(service.itensCarrinho()).toEqual([]);
    expect(service.totalItens()).toBe(0);
  });

  it("should update quantity", () => {
    service.adicionarAoCarrinho(mockItem);
    service.atualizarQuantidade(mockItem.id, 5);
    expect(service.itensCarrinho()[0].quantidade).toBe(5);
    expect(service.totalItens()).toBe(5);
  });

  it("should remove item when quantity updated to 0", () => {
    service.adicionarAoCarrinho(mockItem);
    service.atualizarQuantidade(mockItem.id, 0);
    expect(service.itensCarrinho()).toEqual([]);
  });

  it("should remove item when quantity updated to negative", () => {
    service.adicionarAoCarrinho(mockItem);
    service.atualizarQuantidade(mockItem.id, -1);
    expect(service.itensCarrinho()).toEqual([]);
  });

  it("should clear all items", () => {
    service.adicionarAoCarrinho(mockItem);
    service.adicionarAoCarrinho({ ...mockItem, id: 2 });
    service.limparCarrinho();
    expect(service.itensCarrinho()).toEqual([]);
    expect(service.totalItens()).toBe(0);
  });
});
