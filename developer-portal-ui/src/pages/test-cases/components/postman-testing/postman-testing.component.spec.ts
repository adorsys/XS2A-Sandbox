import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PostmanTestingComponent } from './postman-testing.component';
import {Pipe, PipeTransform} from '@angular/core';

describe('PostmanTestingComponent', () => {
  let component: PostmanTestingComponent;
  let fixture: ComponentFixture<PostmanTestingComponent>;

  @Pipe({name: 'translate'})
  class TranslatePipe implements PipeTransform {
    transform(value) {
      const tmp = value.split('.');
      return tmp[1];
    }
  }

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        PostmanTestingComponent,
        TranslatePipe
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PostmanTestingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
