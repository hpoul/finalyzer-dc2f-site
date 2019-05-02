import * as AOS from 'aos';

function event(action: string, category: string, label?: string, value: number = 0) {
    if (window.gtag) {
        window.gtag('event', action, {
            'event_category': category,
            'event_label': label,
            'event_value': value,

        })
    }
}

interface EmailFormElements {
    email?: HTMLFormElement,
    EMAIL?: HTMLFormElement,
    fs_coupon?: HTMLFormElement
}

let trackFormSubmitCallCount = 0;

function trackFormSubmit(form: HTMLFormElement, formTypeLabel: string, onSubmit: (elements: EmailFormElements, email: string) => {logInfo: { [key: string]: string }, handleSubmit: (event: Event) => void}) {
    let calledAt = trackFormSubmitCallCount++;
    form.addEventListener('submit', e => {
        const backendUrl = window.AA.backendUrl;

        const elements = form.elements as EmailFormElements;

        const email = elements.email ? elements.email.value : elements.EMAIL ? elements.EMAIL.value : '';
        const eventLabel = form.dataset['name']
            || ignoreNull(form.querySelector('input[type=submit]') as HTMLElement, el => el.dataset['name'])
            || form.name
            || `unknown${calledAt}`;

        const formAction = onSubmit(elements, email);

        let beaconSent;
        if (navigator.sendBeacon) {
            try {
                beaconSent = navigator.sendBeacon ? navigator.sendBeacon(`${backendUrl}/auth/log`, JSON.stringify({
                    'label': `${formTypeLabel}:${eventLabel}`,
                    'email': email,
                    'info': {'page': window.location.href, ...formAction.logInfo},
                })) ? 1 : 0 : -1;
            } catch (e) {
                console.error('Error trying to sendBeacon!', e);
                beaconSent = -2;
            }
        } else {
            beaconSent = -1;
        }
        event('click', 'button', formTypeLabel, beaconSent);
        event('click', 'button', `${formTypeLabel}:${eventLabel}`, beaconSent);

        formAction.handleSubmit(e);
    });
}

const setupForm = () => {
    document.querySelectorAll('form.email-form').forEach((form: HTMLFormElement) => {

        trackFormSubmit(form, 'signUpEmail', (elements: EmailFormElements, email: string) => {
            const coupon = elements.fs_coupon ? elements.fs_coupon.value : 'FREEMONTH';
            const product = 'anlage-app-premium-sub';
            return {
                logInfo: {
                    'page': window.location.href,
                    'product': product,
                    'fastspring': window.fastspring ? 'exists' : 'missing'
                },
                handleSubmit: (e) => {
                    e.preventDefault();
                    event('InitiateCheckout', 'conversion', `${product}:${coupon}`);
                    if (window.fastspring) {
                        window.fastspring.builder.push({
                            'products': [{'path': product}],
                            'coupon': coupon,
                            'paymentContact': {'email': email},
                            'checkout': true
                        });
                    } else {
                        document.location.href = `/app/#/auth/signUp;email=${email}`;
                    }
                }
            };
        });
    });
};

function setupWeeklyReportForm() {
    document.querySelectorAll('form.email-form-weekly-report').forEach((form: HTMLFormElement) => {
        trackFormSubmit(form, 'subscribeFreeReports',() => {
            return {
                logInfo: {},
                handleSubmit: () => {
                }
            }
        });
    });
}

function requireNotNull<T>(arg: T|undefined|null, message: string = 'Value required to be not null.'): T {
    if (arg === undefined || arg === null) {
        throw message;
    }
    return arg;
}

function ignoreNull<T, U>(arg: T|undefined|null, cb: ((arg: T) => U)): U|undefined|null {
    if (arg === undefined || arg === null) {
        return null;
    }
    return cb(arg);
}

const setupStartButton = () => {
    ignoreNull(document.querySelector('.button[href="#start-element"]'), v => {
        v.addEventListener('click', () => {
            window.gtag('event', 'click', {
                'event_category': 'button',
                'event_label': 'get started',
            });
            setTimeout(() => safeCall(() => {
                const startElement = requireNotNull(document.getElementById('start-element'));
                const startInput = document.getElementById(requireNotNull(startElement.dataset.target, 'start-element has no target defined.'));
                const input = requireNotNull(startInput, `unable to find start-element target ${startElement.dataset.target}`);
                input.focus();
            }));
        });
    });
};

const setupNavbar = () => {
    Array.prototype.slice.call(document.querySelectorAll('.navbar-burger'), 0)
        .forEach((el: HTMLElement) => {
            el.addEventListener('click', () => {
                // Get the target from the "data-target" attribute
                const target = el.dataset.target;
                const $target = requireNotNull(document.getElementById(target as string));

                // Toggle the "is-active" class on both the "navbar-burger" and the "navbar-menu"
                el.classList.toggle('is-active');
                $target.classList.toggle('is-active');
            });
        });
};

const safeCall = (fun: () => void) => {
    try {
        fun();
    } catch (e) {
        console.error('Error calling function', e);
    }
};

function init() {
    AOS.init({
        // Settings that can be overriden on per-element basis, by `data-aos-*` attributes:
        // offset: 120, // offset (in px) from the original trigger point
        delay: 10, // values from 0 to 3000, with step 50ms
        duration: 400, // values from 0 to 3000, with step 50ms
        easing: 'ease', // default easing for AOS animations
        mirror: true,
    });
    safeCall(setupForm);
    safeCall(setupStartButton);
    safeCall(setupNavbar);
    safeCall(setupWeeklyReportForm);

    document.addEventListener('aos:in', (e: any) => {
        const element = e.detail as HTMLElement;
        event('scroll', 'scroll', element.dataset['name'] || (element as HTMLFormElement).value);
    });

}

if (document.readyState !== 'loading') {
    init();
} else {
    document.addEventListener('DOMContentLoaded', init);
}
