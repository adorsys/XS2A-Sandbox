import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { TppsComponent } from './tpps.component';

describe('TppsComponent', () => {
  let component: TppsComponent;
  let fixture: ComponentFixture<TppsComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ TppsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TppsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
