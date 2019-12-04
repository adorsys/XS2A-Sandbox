import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RedirectComponent } from './redirect.component';
import { Pipe, PipeTransform } from '@angular/core';
import { NgxImageZoomModule } from 'ngx-image-zoom';

describe('RedirectComponent', () => {
  let component: RedirectComponent;
  let fixture: ComponentFixture<RedirectComponent>;

  @Pipe({ name: 'translate' })
  class TranslatePipe implements PipeTransform {
    transform(value) {
      const tmp = value.split('.');
      return tmp[1];
    }
  }

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RedirectComponent, TranslatePipe],
      imports: [NgxImageZoomModule.forRoot()],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RedirectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
