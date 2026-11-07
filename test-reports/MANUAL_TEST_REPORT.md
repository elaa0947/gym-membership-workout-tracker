# Gym Tracker - Manual Testing Report

Date: 07-11-2026 19:07:38

## Summary
- Total cases: 25
- Passed: 25
- Failed: 0
- Not verified: 0

## Detailed results

### BB-01 - Phone number
- Input/path: 9876543210
- Expected: ACCEPTED
- Actual: ACCEPTED
- Status: **PASS**
- Notes: Valid 10-digit Indian mobile number format starting with 9

### BB-02 - Phone number
- Input/path: 5876543210
- Expected: REJECTED
- Actual: REJECTED
- Status: **PASS**
- Notes: Disallowed starting digit 5; only 6-9 accepted

### BB-03 - Phone number
- Input/path: 987654321
- Expected: REJECTED
- Actual: REJECTED
- Status: **PASS**
- Notes: Length 9 digits is less than required 10 digits

### BB-04 - Phone number
- Input/path: 98765432100
- Expected: REJECTED
- Actual: REJECTED
- Status: **PASS**
- Notes: Length 11 digits exceeds permitted 10 digits

### BB-05 - Phone number
- Input/path: 
- Expected: REJECTED
- Actual: REJECTED
- Status: **PASS**
- Notes: Empty phone number rejected by validation

### PW-01 - Password
- Input/path: Valid account password
- Expected: LOGIN SUCCESS
- Actual: LOGIN SUCCESS
- Status: **PASS**
- Notes: Valid credentials authenticated; session established

### PW-02 - Password
- Input/path: Empty password
- Expected: REJECTED
- Actual: REJECTED
- Status: **PASS**
- Notes: Empty password rejected by front-end and back-end constraints

### PW-03 - Password
- Input/path: abc12 (less than 8 characters)
- Expected: REJECTED
- Actual: REJECTED
- Status: **PASS**
- Notes: Password under 8 characters rejected by validator

### PW-04 - Password
- Input/path: Incorrect password
- Expected: LOGIN REJECTED
- Actual: LOGIN REJECTED
- Status: **PASS**
- Notes: BCrypt hash mismatch returns HTTP 401 Unauthorized

### BW-01 - Body weight
- Input/path: 70
- Expected: ACCEPTED
- Actual: ACCEPTED
- Status: **PASS**
- Notes: 70 kg within valid range (20-350 kg) accepted and stored

### BW-02 - Body weight
- Input/path: Empty
- Expected: REJECTED
- Actual: REJECTED
- Status: **PASS**
- Notes: Empty input blocked by form validation

### BW-03 - Body weight
- Input/path: 0
- Expected: REJECTED
- Actual: REJECTED
- Status: **PASS**
- Notes: Zero body weight rejected (weight must be > 0)

### BW-04 - Body weight
- Input/path: -5
- Expected: REJECTED
- Actual: REJECTED
- Status: **PASS**
- Notes: Negative weight value rejected by domain validation

### DT-01 - Decision table
- Input/path: Plan = STARTER, Duration = 3 months, Price = 2400
- Expected: ACCEPTED
- Actual: ACCEPTED
- Status: **PASS**
- Notes: Starter tier activated for 3 months with valid pricing

### DT-02 - Decision table
- Input/path: Plan = PRO, Duration = 6 months, Price = 4200
- Expected: ACCEPTED
- Actual: ACCEPTED
- Status: **PASS**
- Notes: Pro tier activated for 6 months with standard pricing

### DT-03 - Decision table
- Input/path: Plan = ELITE, Duration = 12 months, Price = 7200
- Expected: ACCEPTED
- Actual: ACCEPTED
- Status: **PASS**
- Notes: Elite annual tier activated for 12 months

### DT-04 - Decision table
- Input/path: Plan = INVALID_TIER, Duration = 6 months, Price = 4200
- Expected: REJECTED
- Actual: REJECTED
- Status: **PASS**
- Notes: Unknown plan name rejected with invalid tier message

### DT-05 - Decision table
- Input/path: Plan = [EMPTY], Duration = 0, Price = 0
- Expected: REJECTED
- Actual: REJECTED
- Status: **PASS**
- Notes: Blank plan parameter rejected by requirement check

### WB-01 - Basis path
- Input/path: Path 1: getMembership with non-existent memberId (id = 9999)
- Expected: MEMBER NOT FOUND
- Actual: MEMBER NOT FOUND
- Status: **PASS**
- Notes: Throws IllegalArgumentException: Member not found

### WB-02 - Basis path
- Input/path: Path 2: Member with status = NONE / null plan configuration
- Expected: STATUS INACTIVE
- Actual: STATUS INACTIVE
- Status: **PASS**
- Notes: Returns INACTIVE status with 0 days remaining

### WB-03 - Basis path
- Input/path: Path 3: Member with expired membership (endDate < today)
- Expected: STATUS EXPIRED
- Actual: STATUS EXPIRED
- Status: **PASS**
- Notes: Calculates 0 days remaining and marks status EXPIRED

### WB-04 - Basis path
- Input/path: Path 4: Active membership with STARTER tier
- Expected: STARTER PLAN ACTIVE
- Actual: STARTER PLAN ACTIVE
- Status: **PASS**
- Notes: Formats plan name as Starter Plan with remaining days

### WB-05 - Basis path
- Input/path: Path 5: Active membership with PRO tier
- Expected: PRO PLAN ACTIVE
- Actual: PRO PLAN ACTIVE
- Status: **PASS**
- Notes: Formats plan name as Pro Plan with remaining days

### WB-06 - Basis path
- Input/path: Path 6: Active membership with ELITE tier
- Expected: ELITE PLAN ACTIVE
- Actual: ELITE PLAN ACTIVE
- Status: **PASS**
- Notes: Formats plan name as Elite Plan with remaining days

### WB-07 - Basis path
- Input/path: Path 7: Active membership with custom tier fallback
- Expected: CUSTOM PLAN ACTIVE
- Actual: CUSTOM PLAN ACTIVE
- Status: **PASS**
- Notes: Fallback default branch formats as {plan} Plan
