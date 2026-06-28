import { describe, it, expect, beforeEach, afterEach, vi } from "vitest";
import { TestBed } from "@angular/core/testing";
import { of, throwError } from "rxjs";
import { ProdutoService, ProdutoRequest } from "./produto.service";
import { ApiService } from "./api.service";

const mockApiService = {
  get: vi.fn(),
  post: vi.fn(),
  put: vi.fn(),
  delete: vi.fn(),
};

describe("ProdutoService", () => {
  let service: ProdutoService;

  beforeEach(() => {
    localStorage.clear();
    vi.clearAllMocks();

    TestBed.configureTestingModule({
      providers: [
        ProdutoService,
        { provide: ApiService, useValue: mockApiService },
      ],
    });

    service = TestBed.inject(ProdutoService);
  });

  afterEach(() => {
    localStorage.clear();
  });

  it("should create", () => {
    expect(service).toBeTruthy();
  });

  it("should initialize local products in constructor", () => {
    const stored = localStorage.getItem("mybuddy_produtos_local");
    expect(stored).not.toBeNull();
    const prods = JSON.parse(stored!);
    expect(prods.length).toBeGreaterThanOrEqual(16);
  });

  describe("buscarComFiltros", () => {
    it("should call api.get and return content array", () => {
      const mockContent = [{ id: 1, nome: "Ração" }];
      mockApiService.get.mockReturnValue(of({ content: mockContent }));

      let result: any[] = [];
      service.buscarComFiltros({}).subscribe((data) => (result = data));

      expect(mockApiService.get).toHaveBeenCalledWith("produtos");
      expect(result).toEqual(mockContent);
    });

    it("should return array directly if no content property", () => {
      const mockList = [{ id: 2, nome: "Brinquedo" }];
      mockApiService.get.mockReturnValue(of(mockList));

      let result: any[] = [];
      service.buscarComFiltros({}).subscribe((data) => (result = data));

      expect(result).toEqual(mockList);
    });

    it("should build query params from filtros", () => {
      mockApiService.get.mockReturnValue(of({ content: [] }));

      service.buscarComFiltros({ busca: "ração", categoriaId: 1 }).subscribe();

      const path = mockApiService.get.mock.calls[0][0] as string;
      expect(path).toContain("busca=");
      expect(path).toContain("categoriaId=1");
    });

    it("should fall back to localStorage on API error", () => {
      mockApiService.get.mockReturnValue(throwError(() => new Error("Network error")));

      let result: any[] = [];
      service.buscarComFiltros({}).subscribe((data) => (result = data));

      expect(result.length).toBeGreaterThanOrEqual(16);
    });

    it("should filter by busca term on fallback", () => {
      mockApiService.get.mockReturnValue(throwError(() => new Error("err")));

      let result: any[] = [];
      service.buscarComFiltros({ busca: "racao" }).subscribe((data) => (result = data));

      result.forEach((p) => {
        const textoUnido = [p.nome, p.descricao, p.categoriaNome].join(" ").toLowerCase();
        const normalizado = textoUnido.normalize("NFD").replace(/[̀-ͯ]/g, "");
        expect(normalizado).toContain("racao");
      });
    });

    it("should filter by precoMax on fallback", () => {
      mockApiService.get.mockReturnValue(throwError(() => new Error("err")));

      let result: any[] = [];
      service.buscarComFiltros({ precoMax: 50 }).subscribe((data) => (result = data));

      result.forEach((p) => expect(p.preco).toBeLessThanOrEqual(50));
    });
  });

  describe("buscarPorId", () => {
    it("should call api.get with correct path", () => {
      mockApiService.get.mockReturnValue(of({ id: 1, nome: "Ração" }));

      let result: any;
      service.buscarPorId(1).subscribe((data) => (result = data));

      expect(mockApiService.get).toHaveBeenCalledWith("produtos/1");
      expect(result.id).toBe(1);
    });

    it("should fall back to localStorage product on API error", () => {
      mockApiService.get.mockReturnValue(throwError(() => new Error("err")));

      let result: any;
      service.buscarPorId(1).subscribe((data) => (result = data));

      expect(result).toBeDefined();
      expect(result.id).toBe(1);
    });

    it("should throw error when id not found in localStorage", () => {
      mockApiService.get.mockReturnValue(throwError(() => new Error("err")));

      let error: any;
      service.buscarPorId(99999).subscribe({
        error: (err) => (error = err),
      });

      expect(error).toBeDefined();
      expect(error.message).toContain("não encontrado");
    });
  });

  describe("criar", () => {
    const request: ProdutoRequest = {
      nome: "Produto Teste",
      descricao: "Desc",
      preco: 50,
      estoque: 10,
      subCategoriaId: 1,
      imagens: [],
    };

    it("should call api.post and return response", () => {
      const mockResponse = { id: 100, ...request };
      mockApiService.post.mockReturnValue(of(mockResponse));

      let result: any;
      service.criar(request).subscribe((data) => (result = data));

      expect(mockApiService.post).toHaveBeenCalledWith("produtos", request);
      expect(result.id).toBe(100);
    });

    it("should fall back to localStorage on API error", () => {
      mockApiService.post.mockReturnValue(throwError(() => new Error("err")));

      let result: any;
      service.criar(request).subscribe((data) => (result = data));

      expect(result).toBeDefined();
      expect(result.nome).toBe("Produto Teste");
    });
  });

  describe("atualizar", () => {
    const request: ProdutoRequest = {
      nome: "Ração Atualizada",
      descricao: "Desc",
      preco: 200,
      estoque: 5,
      subCategoriaId: 1,
      imagens: ["/img.jpg"],
    };

    it("should call api.put with correct path", () => {
      mockApiService.put.mockReturnValue(of({ id: 1, ...request }));

      let result: any;
      service.atualizar(1, request).subscribe((data) => (result = data));

      expect(mockApiService.put).toHaveBeenCalledWith("produtos/1", request);
    });

    it("should fall back to localStorage update on API error", () => {
      mockApiService.put.mockReturnValue(throwError(() => new Error("err")));

      let result: any;
      service.atualizar(1, request).subscribe((data) => (result = data));

      expect(result).toBeDefined();
      expect(result.nome).toBe("Ração Atualizada");
    });
  });

  describe("deletar", () => {
    it("should call api.delete with correct path", () => {
      mockApiService.delete.mockReturnValue(of(undefined));

      service.deletar(1).subscribe();

      expect(mockApiService.delete).toHaveBeenCalledWith("produtos/1");
    });

    it("should fall back to localStorage delete on API error", () => {
      mockApiService.delete.mockReturnValue(throwError(() => new Error("err")));

      let completed = false;
      service.deletar(1).subscribe({ complete: () => (completed = true) });

      expect(completed).toBe(true);
      const stored = JSON.parse(localStorage.getItem("mybuddy_produtos_local") || "[]");
      const found = stored.find((p: any) => p.id === 1);
      expect(found).toBeUndefined();
    });
  });

  describe("buscarCategorias", () => {
    it("should call api.get for categorias", () => {
      const mockCats = [{ id: 1, nome: "Alimentação" }];
      mockApiService.get.mockReturnValue(of(mockCats));

      let result: any[] = [];
      service.buscarCategorias().subscribe((data) => (result = data));

      expect(mockApiService.get).toHaveBeenCalledWith("categorias");
      expect(result).toEqual(mockCats);
    });

    it("should return mock categories on API error", () => {
      mockApiService.get.mockReturnValue(throwError(() => new Error("err")));

      let result: any[] = [];
      service.buscarCategorias().subscribe((data) => (result = data));

      expect(result.length).toBe(5);
      expect(result[0].nome).toBe("Alimentação");
    });
  });

  describe("avaliarProduto", () => {
    it("should call api.post for avaliacao", () => {
      const avaliacao = { nota: 5, comentario: "Excelente!" };
      mockApiService.post.mockReturnValue(of({ success: true }));

      let result: any;
      service.avaliarProduto(1, avaliacao).subscribe((data) => (result = data));

      expect(mockApiService.post).toHaveBeenCalledWith("produtos/1/avaliacoes", avaliacao);
    });

    it("should update local product avaliacao on API error", () => {
      mockApiService.post.mockReturnValue(throwError(() => new Error("err")));

      const avaliacao = { nota: 4, comentario: "Bom produto" };
      let result: any;
      service.avaliarProduto(1, avaliacao).subscribe((data) => (result = data));

      expect(result).toBeDefined();
      expect(result.avaliacoes).toBeDefined();
      expect(result.avaliacoes[result.avaliacoes.length - 1].nota).toBe(4);
    });
  });
});
