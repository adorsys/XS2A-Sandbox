import { Component, Input } from '@angular/core';

@Component({
  // Disable tslint selector naming rule for markdown, since it's a imported component
  // tslint:disable-next-line
  selector: 'markdown',
  template: '<span>markdown</span>',
})
export class MockMarkdownComponent {
  @Input()
  src: string;
}
