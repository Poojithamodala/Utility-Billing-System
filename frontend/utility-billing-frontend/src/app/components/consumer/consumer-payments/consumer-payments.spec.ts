import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsumerPayments } from './consumer-payments';

describe('ConsumerPayments', () => {
  let component: ConsumerPayments;
  let fixture: ComponentFixture<ConsumerPayments>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConsumerPayments]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConsumerPayments);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
