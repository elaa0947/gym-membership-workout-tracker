const memberId = localStorage.getItem("memberId");
if (!memberId) location.replace("../login.html");
const key = `corely_workouts_${memberId}`;
const exercises = [
  {name:"Bodyweight squats",detail:"3 sets · 12 reps",tag:"LEGS"},
  {name:"Push-ups",detail:"3 sets · 10 reps",tag:"CHEST"},
  {name:"Dumbbell rows",detail:"3 sets · 10 reps each side",tag:"BACK"},
  {name:"Plank",detail:"3 sets · 30 seconds",tag:"CORE"},
  {name:"Brisk treadmill walk",detail:"10 minutes",tag:"CARDIO"}
];
const read = () => { try { return JSON.parse(localStorage.getItem(key) || "[]"); } catch { return []; } };
const escapeHTML = value => String(value).replace(/[&<>"']/g, c => ({"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#39;"}[c]));
const list = document.getElementById("exerciseList");
exercises.forEach((e,i) => {
  const row = document.createElement("label");
  row.className = "fit-exercise";
  row.innerHTML = `<input type="checkbox" data-exercise="${i}"><span><strong>${escapeHTML(e.name)}</strong><small>${escapeHTML(e.detail)}</small></span><span class="fit-tag">${e.tag}</span>`;
  list.appendChild(row);
});
function render() {
  const done = [...list.querySelectorAll("input:checked")].length;
  const sessions = read();
  document.getElementById("workoutCount").textContent = `${done} of ${exercises.length} exercises completed`;
  document.getElementById("exerciseDone").textContent = done;
  document.getElementById("sessionTotal").textContent = sessions.length;
  document.getElementById("exerciseBar").style.width = `${done / exercises.length * 100}%`;
  document.getElementById("workoutHistory").innerHTML = sessions.length ? sessions.slice().reverse().slice(0,8).map(s => `<div class="fit-history-row"><span><strong>${escapeHTML(s.date)}</strong><small>${s.exercises.length} exercises completed</small></span><span class="fit-history-value">${s.exercises.length}/${exercises.length}</span></div>`).join("") : '<p class="fit-empty">No workouts saved yet. Complete a session to start your history.</p>';
}
list.addEventListener("change", render);
document.getElementById("saveWorkout").addEventListener("click", () => {
  const completed = [...list.querySelectorAll("input:checked")].map(input => exercises[Number(input.dataset.exercise)].name);
  if (!completed.length) { document.getElementById("workoutMessage").textContent = "Complete at least one exercise before saving."; return; }
  const sessions = read();
  sessions.push({date:new Date().toLocaleDateString("en-CA"), exercises:completed, savedAt:new Date().toISOString()});
  localStorage.setItem(key, JSON.stringify(sessions));
  document.getElementById("workoutMessage").textContent = "Workout saved on this browser.";
  list.querySelectorAll("input").forEach(input => input.checked = false);
  render();
});
(async () => {
  try {
    const r = await fetch(`http://localhost:8080/api/members/profile/${encodeURIComponent(memberId)}`);
    if (r.ok) { const m = await r.json(); document.getElementById("userName").textContent = m.name || "Member"; document.getElementById("userAvatar").textContent = (m.name || "M").trim().charAt(0).toUpperCase(); }
  } catch {}
})();
render();
