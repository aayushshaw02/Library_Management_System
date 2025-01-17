package aayush.librarymanagementsystem.service.serviceImpl;

import aayush.librarymanagementsystem.dto.Account;
import aayush.librarymanagementsystem.entity.AccountEntity;
import aayush.librarymanagementsystem.repository.AccountRepository;
import aayush.librarymanagementsystem.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<Account> getAccountList(Pageable pageable) {
        Page<AccountEntity> accountEntities = accountRepository.findAll(
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "email"))
                ));

        return accountEntities.map(AccountEntity::toDto);
    }

    @Override
    public Page<Account> getAccountListByRole(String role, Pageable pageable) {
        Page<AccountEntity> accountEntities = accountRepository.getAccountsByRole(
                role,
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "email"))
                ));

        return accountEntities.map(AccountEntity::toDto);
    }

    @Override
    public Page<Account> getAccountListByRole(String keyword, String role, Pageable pageable) {
        Page<AccountEntity> accountEntities = accountRepository.getAccountsByRoleAndKeyword(
                keyword,
                role,
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "email"))
                ));

        return accountEntities.map(AccountEntity::toDto);
    }

    @Override
    public Optional<Account> getAccountById(long id) {
        AccountEntity account = accountRepository.findById(id).orElse(null);
        if (account != null) {
            return Optional.of(account.toDto());
        }

        return Optional.empty();
    }

    @Override
    public Optional<Account> getAccountByEmail(String email) {
        AccountEntity account = accountRepository.findByEmail(email).orElse(null);
        if (account != null) {
            return Optional.of(account.toDto());
        }

        return Optional.empty();
    }

    @Override
    public Optional<Account> saveAccount(Account account) {
        AccountEntity accountEntity = AccountEntity.fromDto(account);
        if (accountRepository.existsByEmail(accountEntity.getEmail())) {
            return Optional.empty();
        }

        // Encode the password before saving
        accountEntity.setPassword(passwordEncoder.encode(account.getPassword()));

        accountEntity = accountRepository.save(accountEntity);
        return Optional.of(accountEntity.toDto());
    }

    @Override
    public Optional<Account> updateAccountInfo(Long id, Account account) {
        if (accountRepository.existsById(id)) {
            AccountEntity accountEntity = accountRepository.getReferenceById(id);
            accountEntity.setFirstName(account.getFirstName());
            accountEntity.setLastName(account.getLastName());
            accountEntity.setEmail(account.getEmail());
            accountEntity = accountRepository.save(accountEntity);
            return Optional.of(accountEntity.toDto());
        }

        return Optional.empty();
    }

    @Override
    public boolean deleteAccount(long id) {
        if (accountRepository.existsById(id)) {
            accountRepository.deleteById(id);
            return true;
        }
        return false;
    }
}