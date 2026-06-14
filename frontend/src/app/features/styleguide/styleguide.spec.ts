import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Styleguide } from './styleguide';

describe('Styleguide', () => {
  let component: Styleguide;
  let fixture: ComponentFixture<Styleguide>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Styleguide],
    }).compileComponents();

    fixture = TestBed.createComponent(Styleguide);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
