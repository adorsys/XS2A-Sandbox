import { Component } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ConvertBalancePipe } from './convertBalance.pipe';
import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';

@Component({
  template: '<div> {{ balance | convertBalance }} </div>',
})
export class ConvertBalancePipeHostComponent {
  balance: number;
}

describe('ConvertBalancePipe inside a Component', () => {
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ConvertBalancePipe, ConvertBalancePipeHostComponent],
    }).compileComponents();
  }));

  let fixture: ComponentFixture<ConvertBalancePipeHostComponent>;
  let debugElement: DebugElement;
  let component: ConvertBalancePipeHostComponent;

  beforeEach(() => {
    fixture = TestBed.createComponent(ConvertBalancePipeHostComponent);
    debugElement = fixture.debugElement;
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(fixture).toBeTruthy();
  });

  it('should Display 0,44', () => {
    component.balance = 0.44;
    fixture.detectChanges();

    const div: HTMLDivElement = debugElement.query(By.css('div')).nativeElement;

    expect(div.textContent.trim()).toEqual('0,44');
  });

  it('should Display 10.000.000,00', () => {
    component.balance = 10000000;
    fixture.detectChanges();

    const div: HTMLDivElement = debugElement.query(By.css('div')).nativeElement;

    expect(div.textContent.trim()).toEqual('10.000.000,00');
  });

  it('should Display 10.000.000,50', () => {
    component.balance = 10000000.5;
    fixture.detectChanges();

    const div: HTMLDivElement = debugElement.query(By.css('div')).nativeElement;

    expect(div.textContent.trim()).toEqual('10.000.000,50');
  });
});
