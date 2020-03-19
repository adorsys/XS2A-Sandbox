import {TestBed} from '@angular/core/testing';

import {MarkdownStylingService} from './markdown-styling.service';
import {MarkdownModule, MarkdownService} from "ngx-markdown";

describe('MarkdownStylingService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    providers: [MarkdownService],
    imports: [MarkdownModule.forRoot()]
  }));

  it('should be created', () => {
    const service: MarkdownStylingService = TestBed.get(MarkdownStylingService);
    expect(service).toBeTruthy();
  });
});
