import { describe, it, expect, beforeEach, afterEach } from "vitest";
import { TestBed } from "@angular/core/testing";
import {
  HttpTestingController,
  provideHttpClientTesting,
} from "@angular/common/http/testing";
import { provideHttpClient, HttpParams } from "@angular/common/http";
import { ApiService } from "./api.service";
import { environment } from "../../../environments/environment";

describe("ApiService", () => {
  let service: ApiService;
  let httpMock: HttpTestingController;
  const base = environment.apiUrl;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ApiService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(ApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });

  it("should expose baseUrl from environment", () => {
    expect(service.baseUrl).toBe(environment.apiUrl);
  });

  it("should perform GET request", () => {
    service.get<string[]>("pets").subscribe((res) => {
      expect(res).toEqual(["a", "b"]);
    });
    const req = httpMock.expectOne(`${base}pets`);
    expect(req.request.method).toBe("GET");
    req.flush(["a", "b"]);
  });

  it("should perform GET request with HttpParams", () => {
    const params = new HttpParams().set("page", "1");
    service.get<any>("pets", params).subscribe();
    const req = httpMock.expectOne(`${base}pets?page=1`);
    expect(req.request.method).toBe("GET");
    req.flush([]);
  });

  it("should perform POST request", () => {
    const body = { nome: "Rex" };
    service.post<any>("pets", body).subscribe((res) => {
      expect(res.id).toBe(1);
    });
    const req = httpMock.expectOne(`${base}pets`);
    expect(req.request.method).toBe("POST");
    expect(req.request.body).toEqual(body);
    req.flush({ id: 1, ...body });
  });

  it("should perform PUT request", () => {
    const body = { nome: "Rex Atualizado" };
    service.put<any>("pets/1", body).subscribe();
    const req = httpMock.expectOne(`${base}pets/1`);
    expect(req.request.method).toBe("PUT");
    expect(req.request.body).toEqual(body);
    req.flush({ id: 1, ...body });
  });

  it("should perform DELETE request", () => {
    service.delete<void>("pets/1").subscribe();
    const req = httpMock.expectOne(`${base}pets/1`);
    expect(req.request.method).toBe("DELETE");
    req.flush(null);
  });
});
