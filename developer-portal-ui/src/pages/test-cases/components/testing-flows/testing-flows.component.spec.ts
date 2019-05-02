import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TestingFlowsComponent } from './testing-flows.component';

describe('TestingFlowsComponent', () => {
  let component: TestingFlowsComponent;
  let fixture: ComponentFixture<TestingFlowsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [TestingFlowsComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TestingFlowsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
