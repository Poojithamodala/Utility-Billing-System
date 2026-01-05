import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsumerBills } from './consumer-bills';

describe('ConsumerBills', () => {
  let component: ConsumerBills;
  let fixture: ComponentFixture<ConsumerBills>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConsumerBills]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConsumerBills);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
