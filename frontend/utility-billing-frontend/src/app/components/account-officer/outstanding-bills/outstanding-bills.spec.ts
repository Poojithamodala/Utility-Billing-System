import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OutstandingBills } from './outstanding-bills';

describe('OutstandingBills', () => {
  let component: OutstandingBills;
  let fixture: ComponentFixture<OutstandingBills>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OutstandingBills]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OutstandingBills);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
