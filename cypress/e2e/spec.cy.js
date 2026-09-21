/*describe('Supplier Delivery', () => {
  it('opens the application home page', () => {
    cy.visit('/')

    cy.contains('h1', 'Supplier Delivery')
        .should('be.visible')
  })
})*/

describe('Supplier Delivery', () => {
    let createdSupplierId = null
    beforeEach(() => {
        createdSupplierId = null
        cy.intercept('GET', '/api/suppliers')
            .as('getSuppliers')

        cy.intercept('GET', '/api/products')
            .as('getProducts')

        cy.intercept('GET', '/api/supplier-prices')
            .as('getSupplierPrices')
        cy.visit('/')
    })
    afterEach(() => {
        if (createdSupplierId !== null) {
            cy.request('DELETE', `/api/suppliers/${createdSupplierId}`)
        }
    })
    it('creates a supplier', () => {
        const supplierName = `Cypress supplier ${Date.now()}`

        cy.intercept('POST', '/api/suppliers')
            .as('createSupplier')

        cy.get('#supplierName')
            .type(supplierName)

        cy.get('#supplierName')
            .closest('section')
            .contains('button', 'Добавить')
            .click()

        cy.wait('@createSupplier')
            .its('response')
            .then((response) => {
                expect(response.statusCode)
                    .to.equal(200)

                expect(response.body)
                    .to.have.property('id')

                createdSupplierId = response.body.id
            })

        cy.get('#supplierMessage')
            .should('be.visible')
            .and('have.text', 'Поставщик создан')

        cy.get('#suppliersTable')
            .contains('td', supplierName)
            .should('be.visible')
    })
    it('opens the application home page', () => {
        cy.contains('h1', 'Supplier Delivery')
            .should('be.visible')
    })
    it('loads initial data from the API', () => {
        cy.wait('@getSuppliers')
            .its('response.statusCode')
            .should('equal', 200)

        cy.wait('@getProducts')
            .its('response.statusCode')
            .should('equal', 200)

        cy.wait('@getSupplierPrices')
            .its('response.statusCode')
            .should('equal', 200)
    })
    it('shows the supplier creation field', () => {
        cy.get('#supplierName')
            .should('be.visible')
            .and('have.attr', 'placeholder', 'Название поставщика')
    })
    it('shows a validation error for an empty supplier name', () => {
        cy.intercept('POST', '/api/suppliers')
            .as('createSupplier')

        cy.get('#supplierName')
            .should('have.value', '')

        cy.get('#supplierName')
            .closest('section')
            .contains('button', 'Добавить')
            .click()

        cy.wait('@createSupplier')
            .its('response.statusCode')
            .should('equal', 400)

        cy.get('#supplierMessage')
            .should('be.visible')
            .should(($message) => {
                const message = $message.text().trim()

                expect(message).to.be.oneOf([
                    'Имя поставщика не должно быть пустым',
                    'Имя поставщика должно содержать от 2 до 255 символов',
                ])
            })
    })
    it('shows an error when delivery has no products', () => {
        cy.contains('button', 'Создать поставку')
            .click()

        cy.get('#deliveryMessage')
            .should('be.visible')
            .and('have.text', 'Добавьте хотя бы один товар')
    })
})