window.sleep = function(duration) {
	return new Promise(resolve => setTimeout(resolve, duration));
};

window.translate = function(element, x, y, duration) {
	element.style.transitionDuration = duration + "ms";
	element.style.transform = "translate(" + x + "px," + y + "px)";
}

window.destroyAfter = async function(element, wait) {
	await sleep(wait);
	element.parentNode.removeChild(element);
};

window.sonarEffect = async function(element, wait, className) {
	await sleep(wait);
	element.className += ' ' + className;
	await sleep(3600);
	element.parentNode.removeChild(element);
}

window.animateSonarHit = async function(element) {
	await sleep(200);
	for (let i = 0; i < 5; i++) {
		element.style.border = '2px solid gray';
		await sleep(400);
		element.style.border = 'none';
		await sleep(400);
	}
	element.parentNode.removeChild(element);
}

window.animateProjectileHit = async function(element, wait) {
	let old = element.style.border;
	await sleep(wait);
	for (let i = 0; i < 3; i++) {
		element.style.border = '2px solid red';
		await sleep(400);
		element.style.border = old;
		await sleep(400);
	}
	element.parentNode.removeChild(element);
}

window.animateSubmarineHit = async function(element, wait) {
	let old = element.style.border;
	await sleep(wait);
	for (let i = 0; i < 3; i++) {
		element.style.border = '2px solid red';
		await sleep(400);
		element.style.border = old;
		await sleep(400);
	}
}

window.animateExplosion = async function(element, wait) {
	await sleep(wait);
	element.className += ' explosionSprite';
	await sleep(1700);
	element.parentNode.removeChild(element);
}