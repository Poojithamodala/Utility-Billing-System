import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BillingOfficerBills } from './billing-officer-bills';

describe('BillingOfficerBills', () => {
  let component: BillingOfficerBills;
  let fixture: ComponentFixture<BillingOfficerBills>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BillingOfficerBills]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BillingOfficerBills);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
