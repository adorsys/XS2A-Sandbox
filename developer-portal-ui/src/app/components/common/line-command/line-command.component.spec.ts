import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {LineCommandComponent} from './line-command.component';

describe('LineCommandComponent', () => {
  let component: LineCommandComponent;
  let fixture: ComponentFixture<LineCommandComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [LineCommandComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LineCommandComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
