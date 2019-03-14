import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FaqsPageComponent } from './faqs-page.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { HeaderComponent } from '../../common/header/header.component';
import { MockMarkdownComponent } from '../../common/mock-markdown.component';
import { FormsModule } from '@angular/forms';

describe('FaqsPageComponent', () => {
  let component: FaqsPageComponent;
  let fixture: ComponentFixture<FaqsPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [FaqsPageComponent, HeaderComponent, MockMarkdownComponent],
      imports: [HttpClientTestingModule, RouterTestingModule, FormsModule],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FaqsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
