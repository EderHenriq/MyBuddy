import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HeaderLandingPage } from './header-landing-page';

describe('HeaderLandingPage', () => {
  let component: HeaderLandingPage;
  let fixture: ComponentFixture<HeaderLandingPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HeaderLandingPage],
    }).compileComponents();

    fixture = TestBed.createComponent(HeaderLandingPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
