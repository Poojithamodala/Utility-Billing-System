import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsumerConnections } from './consumer-connections';

describe('ConsumerConnections', () => {
  let component: ConsumerConnections;
  let fixture: ComponentFixture<ConsumerConnections>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConsumerConnections]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConsumerConnections);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
