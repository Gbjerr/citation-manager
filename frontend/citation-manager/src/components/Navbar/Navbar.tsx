import type { TokenPair } from '../../types/types'
import { Link } from 'react-router-dom'
import './Navbar.css'
import { API_BASE_URL } from '../../utils/api.ts';

type NavbarProps = {
    isAuthenticated: boolean,
    tokenPair: TokenPair
    clear: () => void
}

/**
 * The navigation bar component, used for easy access to log in, sign up, log out etc.
 */
export const Navbar = ({isAuthenticated, tokenPair, clear}: NavbarProps) => {

        const handleLogout = async (clear: () => void, tokenPair: TokenPair) => {
        try {
            const headers: Record<string, string> = {"Content-Type": "application/json"};
            if(tokenPair?.accessToken) {
                headers["Authorization"] = `Bearer ${tokenPair.accessToken}`;
            }

            const res = await fetch(`${API_BASE_URL}/api/auth/logout`, {
                method: "POST",
                headers: headers,
                body: JSON.stringify({
                    refreshToken: tokenPair.refreshToken
                })
            });
            if(!res.ok) console.log(res.status);

        } catch (e) {
            console.error(e);
        } finally {
            clear();
        }
    }

    return (
        <nav>
            <div className="navLeft">
                <Link to="/"><img src='/logo/cm_logo.png' className='logo' /></Link>
            </div>
            <div className="navRight">
                {isAuthenticated ? (
                    <button
                        className="logoutBtn"
                        onClick={() => handleLogout(clear, tokenPair)}
                    >
                        Logout
                    </button>
                ) : (
                    <div className='rightBtns'>
                            <Link to='/login'>
                                <button className="logInBtn" >Log in</button>
                            </Link>
                            <Link to='/signup'>
                                <button className="signInBtn">Sign up</button>
                            </Link>
                    </div>
                )}
            </div>
        </nav>
    );
}