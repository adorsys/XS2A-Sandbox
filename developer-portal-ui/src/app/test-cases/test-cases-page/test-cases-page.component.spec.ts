import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { TestCasesPageComponent } from './test-cases-page.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { HeaderComponent } from '../../common/header/header.component';
import { MockMarkdownComponent } from '../../common/mock-markdown.component';
import { FormsModule } from '@angular/forms';

describe('TestCasesPageComponent', () => {
  let component: TestCasesPageComponent;
  let fixture: ComponentFixture<TestCasesPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        TestCasesPageComponent,
        HeaderComponent,
        MockMarkdownComponent,
      ],
      imports: [HttpClientTestingModule, RouterTestingModule, FormsModule],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TestCasesPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
