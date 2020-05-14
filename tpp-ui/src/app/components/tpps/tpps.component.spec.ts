import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TppsComponent } from './tpps.component';

describe('TppsComponent', () => {
  let component: TppsComponent;
  let fixture: ComponentFixture<TppsComponent>;

  beforeEach(async(() => {
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
