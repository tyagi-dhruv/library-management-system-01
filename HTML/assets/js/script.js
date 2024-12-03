// index.js
document.getElementById("scrollButton").addEventListener("click", function () {
  const nextPage = document.getElementById("page2");
  nextPage.scrollIntoView({ behavior: "smooth" });
});

// person/list.js
