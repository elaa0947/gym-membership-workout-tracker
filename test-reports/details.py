from datetime import datetime
from pathlib import Path
from getpass import getpass
import csv

OUT = Path.cwd() / f"manual_test_results_{datetime.now():%Y%m%d_%H%M%S}"
OUT.mkdir(exist_ok=True)

results = []

TESTS = [
    ("BB-01", "Phone number", "9876543210", "ACCEPTED"),
    ("BB-02", "Phone number", "5876543210", "REJECTED"),
    ("BB-03", "Phone number", "987654321", "REJECTED"),
    ("BB-04", "Phone number", "98765432100", "REJECTED"),
    ("BB-05", "Phone number", "", "REJECTED"),

    ("PW-01", "Password", "Valid account password", "LOGIN SUCCESS"),
    ("PW-02", "Password", "Empty password", "REJECTED"),
    ("PW-03", "Password", "abc12 (less than 8 characters)", "REJECTED"),
    ("PW-04", "Password", "Incorrect password", "LOGIN REJECTED"),

    ("BW-01", "Body weight", "70", "ACCEPTED"),
    ("BW-02", "Body weight", "Empty", "REJECTED"),
    ("BW-03", "Body weight", "0", "REJECTED"),
    ("BW-04", "Body weight", "-5", "REJECTED"),
]

def ask(prompt):
    return input(prompt).strip()

def collect_fixed(test):
    test_id, category, test_input, expected = test

    print("\n" + "=" * 55)
    print(f"TEST: {test_id} | {category}")
    print("Input:", test_input if test_input else "[EMPTY]")
    print("Expected:", expected)

    actual = ask(
        "Enter observed result "
        "(ACCEPTED/REJECTED or LOGIN SUCCESS/LOGIN REJECTED): "
    ).upper()

    notes = ask("Observation/notes (Enter to skip): ")

    status = "PASS" if actual == expected else "FAIL"

    results.append({
        "ID": test_id,
        "Category": category,
        "Input": test_input,
        "Expected": expected,
        "Actual": actual,
        "Status": status,
        "Notes": notes
    })

    print("RESULT:", status)

def collect_custom(test_id, category):
    print("\n" + "=" * 55)
    print(f"TEST: {test_id} | {category}")
    print("Execute the corresponding decision/path in your application.")

    description = ask("Conditions / input / path exercised: ")
    expected = ask("Expected result: ")
    actual = ask("Actual observed result: ")
    notes = ask("Additional notes (Enter to skip): ")

    status = (
        "NOT VERIFIED"
        if not expected or not actual
        else "PASS" if actual.casefold() == expected.casefold()
        else "FAIL"
    )

    results.append({
        "ID": test_id,
        "Category": category,
        "Input": description,
        "Expected": expected,
        "Actual": actual,
        "Status": status,
        "Notes": notes
    })

    print("RESULT:", status)

def save_results():
    csv_file = OUT / "manual_test_results.csv"

    fields = [
        "ID", "Category", "Input", "Expected",
        "Actual", "Status", "Notes"
    ]

    with csv_file.open("w", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(f, fieldnames=fields)
        writer.writeheader()
        writer.writerows(results)

    total = len(results)
    passed = sum(r["Status"] == "PASS" for r in results)
    failed = sum(r["Status"] == "FAIL" for r in results)
    unverified = sum(r["Status"] == "NOT VERIFIED" for r in results)

    report = [
        "# Gym Tracker - Manual Testing Report",
        "",
        f"Date: {datetime.now():%d-%m-%Y %H:%M:%S}",
        "",
        "## Summary",
        f"- Total cases: {total}",
        f"- Passed: {passed}",
        f"- Failed: {failed}",
        f"- Not verified: {unverified}",
        "",
        "## Detailed results",
        ""
    ]

    for r in results:
        report.extend([
            f"### {r['ID']} - {r['Category']}",
            f"- Input/path: {r['Input']}",
            f"- Expected: {r['Expected']}",
            f"- Actual: {r['Actual']}",
            f"- Status: **{r['Status']}**",
            f"- Notes: {r['Notes'] or 'None'}",
            ""
        ])

    (OUT / "MANUAL_TEST_REPORT.md").write_text(
        "\n".join(report), encoding="utf-8"
    )

    print("\n" + "=" * 55)
    print("MANUAL TEST COLLECTION COMPLETE")
    print(f"Total: {total}")
    print(f"Passed: {passed}")
    print(f"Failed: {failed}")
    print(f"Not verified: {unverified}")
    print("Saved folder:", OUT)
    print("Files: manual_test_results.csv, MANUAL_TEST_REPORT.md")

def main():
    print("=" * 55)
    print("CORELY GYM TRACKER - MANUAL TEST COLLECTOR")
    print("=" * 55)
    print("Perform each test in your running application.")
    print("Enter what actually happened, not what should happen.")
    print("Passwords are never saved by this script.")

    print("\nPHONE, PASSWORD AND WEIGHT TESTS")

    for test in TESTS:
        if test[0].startswith("PW-") and test[0] == "PW-01":
            print("\nFor password testing, use your own test account.")
        collect_fixed(test)

    print("\nDECISION TABLE TESTS")
    print("Use your original decision table conditions.")
    for i in range(1, 6):
        collect_custom(f"DT-{i:02d}", "Decision table")

    print("\nBASIS PATH TESTS")
    print("Execute each distinct path in the relevant program logic.")
    for i in range(1, 8):
        collect_custom(f"WB-{i:02d}", "Basis path")

    save_results()

if __name__ == "__main__":
    main()

    