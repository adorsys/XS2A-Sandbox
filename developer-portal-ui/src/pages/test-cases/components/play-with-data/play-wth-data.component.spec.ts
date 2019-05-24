import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PlayWthDataComponent } from './play-wth-data.component';

describe('PlayWthDataComponent', () => {
  let component: PlayWthDataComponent;
  let fixture: ComponentFixture<PlayWthDataComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [PlayWthDataComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PlayWthDataComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
