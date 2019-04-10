import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GrantConsentComponent } from './grant-consent.component';

describe('GrantConsentComponent', () => {
  let component: GrantConsentComponent;
  let fixture: ComponentFixture<GrantConsentComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GrantConsentComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GrantConsentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
