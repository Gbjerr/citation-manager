import { useState } from 'react'
import './Signup.css'
import type { LoginProps } from '../types/types';
import { Link } from 'react-router-dom';
import { API_BASE_URL } from '../utils/api.ts';

async function signupUser(userCredentials: {username: string, email: string, password: string}) {
    return await fetch(`${API_BASE_URL}/api/auth/register`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(userCredentials),
    });
}

/**
 * The sign up UI component which lets users create their accounts.
 */
const Signup = ({ setTokenPair }: LoginProps) => {
    const [username, setUsername] = useState<string>("");
    const [email, setEmail] = useState<string>("");
    const [password, setPassword] = useState<string>("");
    const [confirmPassword, setConfirmPassword] = useState<string>("");
    const [error, setError] = useState<string>("");

    const validatePasswordMatch = () => {
        return password === confirmPassword;
    };

    const doSignupUser = async (event: React.ChangeEvent<HTMLFormElement>) => {
        event.preventDefault();

        if(!validatePasswordMatch()) {
            setError('Entered passwords must match.')
            return;
        }

        const res = await signupUser({username, email, password});
        const data = await res.json();

        if(!res.ok) {
            setError(data.message);
            return;
        }

        setTokenPair(data);
    }

    return (
        <div className='signup-wrapper'>
            <h1>Sign up</h1>

            <form className='signupForm' onSubmit={doSignupUser}>
                <label>
                   <p>Username</p>
                    <input type="text" name="username" required onChange={e => setUsername(e.target.value)}/>
                </label>
                <label>
                   <p>Email</p>
                    <input type="text" name="email" required onChange={e => setEmail(e.target.value)}/>
                </label>
                <label>
                   <p>Password</p>
                    <input type="password" name="password" required onChange={e => setPassword(e.target.value)}/>
                </label>
                <label>
                   <p>Confirm password</p>
                    <input type="password" name="confirmPassword" required onChange={e => setConfirmPassword(e.target.value)}/>
                </label>
                {error && <span className='error'>{error}</span>}
                <p color='red'>Already have an account? {<Link to='/login'>Log In</Link>}</p>
                <div className='submit-wrapper'>
                    <button type="submit">Submit</button>
                </div>
            </form>
        </div>
    );
}

export default Signup;
