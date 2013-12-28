package br.com.mvbos.test.vo;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import br.com.mvbos.nodethunder.annotation.ThunderEntity;
import br.com.mvbos.nodethunder.annotation.ThunderField;

/**
 * 
 * @author Marcus Becker
 * 
 */

@ThunderEntity(propertyName = "id")
public class Client {

	@ThunderField
	private Long id;
	@ThunderField
	private String name;
	@ThunderField
	private Double salary;
	@ThunderField
	private BigDecimal acountBalance;
	@ThunderField
	private Calendar lastCheck;
	@ThunderField
	private Boolean reciveNews;

	@ThunderField(converter = PhonesConverter.class)
	private List<String> phones;

	public Client() {
		super();
	}

	public Client(Long id, String name, Double salary,
			BigDecimal acountBalance, Calendar lastCheck, Boolean reciveNews) {
		super();
		this.id = id;
		this.name = name;
		this.salary = salary;
		this.acountBalance = acountBalance;
		this.lastCheck = lastCheck;
		this.reciveNews = reciveNews;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getSalary() {
		return salary;
	}

	public void setSalary(Double salary) {
		this.salary = salary;
	}

	public BigDecimal getAcountBalance() {
		return acountBalance;
	}

	public void setAcountBalance(BigDecimal acountBalance) {
		this.acountBalance = acountBalance;
	}

	public Calendar getLastCheck() {
		return lastCheck;
	}

	public void setLastCheck(Calendar lastCheck) {
		this.lastCheck = lastCheck;
	}

	public Boolean getReciveNews() {
		return reciveNews;
	}

	public void setReciveNews(Boolean reciveNews) {
		this.reciveNews = reciveNews;
	}

	public List<String> getPhones() {
		return phones;
	}

	public void setPhones(List<String> phones) {
		this.phones = phones;
	}

	@Override
	public String toString() {
		return "Client [id=" + id + ", name=" + name + ", salary=" + salary
				+ ", acountBalance=" + acountBalance + ", lastCheck="
				+ lastCheck.getTimeInMillis() + ", reciveNews=" + reciveNews + ", phones="
				+ phones + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Client other = (Client) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
