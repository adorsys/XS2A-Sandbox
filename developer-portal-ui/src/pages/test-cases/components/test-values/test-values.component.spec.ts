import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TestValuesComponent } from './test-values.component';

describe('PostmanTestingComponent', () => {
  let component: TestValuesComponent;
  let fixture: ComponentFixture<TestValuesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [TestValuesComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TestValuesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
