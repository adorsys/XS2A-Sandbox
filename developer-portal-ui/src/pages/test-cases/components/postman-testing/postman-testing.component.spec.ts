import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PostmanTestingComponent } from './postman-testing.component';

describe('PostmanTestingComponent', () => {
  let component: PostmanTestingComponent;
  let fixture: ComponentFixture<PostmanTestingComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [PostmanTestingComponent],
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
