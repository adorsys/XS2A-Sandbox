import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { DeveloperPortalPageComponent } from './developer-portal-page.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { HeaderComponent } from '../../common/header/header.component';
import { MockMarkdownComponent } from '../../common/mock-markdown.component';
import { FormsModule } from '@angular/forms';

describe('DeveloperPortalPageComponent', () => {
  let component: DeveloperPortalPageComponent;
  let fixture: ComponentFixture<DeveloperPortalPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        DeveloperPortalPageComponent,
        HeaderComponent,
        MockMarkdownComponent,
      ],
      imports: [HttpClientTestingModule, RouterTestingModule, FormsModule],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DeveloperPortalPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
